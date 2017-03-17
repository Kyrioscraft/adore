package cn.kyrioscraft.data.service;

import com.google.common.collect.Lists;
import cn.kyrioscraft.data.common.ApplicationSettings;
import cn.kyrioscraft.data.dto.AlphabetDTO;
import cn.kyrioscraft.data.dto.PostDTO;
import cn.kyrioscraft.data.dto.TagDTO;
import cn.kyrioscraft.data.enums.ContentType;
import cn.kyrioscraft.data.enums.PostDisplayType;
import cn.kyrioscraft.data.exceptions.DuplicatePostNameException;
import cn.kyrioscraft.data.exceptions.PostNotFoundException;
import cn.kyrioscraft.data.exceptions.TagNotFoundException;
import cn.kyrioscraft.data.model.*;
import cn.kyrioscraft.data.repository.LikeRepository;
import cn.kyrioscraft.data.repository.PostImageRepository;
import cn.kyrioscraft.data.repository.PostRepository;
import cn.kyrioscraft.data.repository.TagRepository;
import cn.kyrioscraft.data.utils.PostUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by kyrioscraft on 6/1/16.
 */
@Service("postService")
@Transactional
public class PostServiceImpl implements PostService {

    private static final Logger logger = LoggerFactory.getLogger(PostServiceImpl.class);

    private PostRepository postRepository;
    private TagRepository tagRepository;
    private LikeRepository likeRepository;
    private PostImageRepository postImageRepository;
    private ApplicationSettings applicationSettings;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, TagRepository tagRepository, LikeRepository likeRepository, PostImageRepository postImageRepository, ApplicationSettings applicationSettings) {
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
        this.likeRepository = likeRepository;
        this.postImageRepository = postImageRepository;
        this.applicationSettings = applicationSettings;
    }

    @PersistenceContext
    private EntityManager em;

    //region Add / UpdatePost

    @Transactional(rollbackFor = DuplicatePostNameException.class)
    @Override
    public Post add(PostDTO postDTO) throws DuplicatePostNameException {
        Post post;
        try {
            post = postRepository.save(PostUtils.postDtoToPost(postDTO));

        } catch (Exception e) {
            throw new DuplicatePostNameException("Duplicate Post Name for Post Title: " +
                    postDTO.getPostTitle());
        }

        if (postDTO.getTags() != null) {

            saveNewTagsToDataBase(postDTO);

            post.setTags(new HashSet<>());
            for (TagDTO tagDTO : postDTO.getTags()) {
                Tag tag = tagRepository.findByTagValueIgnoreCase(tagDTO.getTagValue());
                post.getTags().add(tag);
            }
        }

        return post;
    }

    @Transactional(rollbackFor = PostNotFoundException.class)
    @Override
    public Post update(PostDTO postDTO) throws PostNotFoundException {

        Post post = postRepository.findByPostId(postDTO.getPostId());
        post.update(postDTO.getPostTitle(), postDTO.getPostContent(), postDTO.getIsPublished(), postDTO.getDisplayType());

        saveNewTagsToDataBase(postDTO);

        post.getTags().clear();
        for (TagDTO tagDTO : postDTO.getTags()) {
            Tag tag = tagRepository.findByTagValueIgnoreCase(tagDTO.getTagValue());

            if (!post.getTags().contains(tag))
                post.getTags().add(tag);
        }

        return post;
    }

    //endregion

    // region Likes

    @Transactional(readOnly = true)
    @Override
    public List<Post> getPostsByUserLikes(Long userId) {
        List<Post> posts;
        if (likeRepository.findLikedPostIds(userId).size() == 0)
            return null;
        else
            posts = em.createNamedQuery("Post.getByPostIds", Post.class)
                    .setParameter("postIds", likeRepository.findLikedPostIds(userId))
                    .getResultList();
        return posts;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Post> getPagedLikedPosts(long userId, int pageNumber, int pageSize) {
        List<Post> posts = em.createNamedQuery("Post.getByPostIds", Post.class)
                .setParameter("postIds", likeRepository.findLikedPostIds(userId))
                .setFirstResult(pageNumber * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
        return posts;
    }

    @Transactional
    @Override
    public int addPostLike(long userId, long postId) {
        int incrementValue = 1;
        Post post = postRepository.findByPostId(postId);
        Optional<Long> likeId = likeRepository.findPostLikeIdByUserId(userId, postId);
        if (likeId.isPresent()) {
            incrementValue = -1;
            likeRepository.delete(likeId.get());
        } else {
            Like like = new Like(userId, postId, ContentType.POST);
            likeRepository.save(like);
        }
        post.updateLikes(incrementValue);
        return incrementValue;
    }

    // endregion

    //region Get Posts

    @Transactional(readOnly = true)
    @Override
    public Post getPostById(Long ID) throws PostNotFoundException {
        Post found = postRepository.findByPostId(ID);
        if (found == null) {
            logger.debug("No post found with id: {}", ID);
            throw new PostNotFoundException("No post found with id: " + ID);
        }
        return found;
    }

    @Transactional(readOnly = true)
    @Override
    public Post getPost(String postName) throws PostNotFoundException {
        Post found = postRepository.findByPostNameIgnoreCase(postName);
        if (found == null) {
            logger.debug("No post found with id: {}", postName);
            throw new PostNotFoundException("No post found with id: " + postName);
        } else {
            if (found.getDisplayType().equals(PostDisplayType.MULTIPHOTO_POST))
                found.setPostImages(this.getPostImages(found.getPostId()));
            if (found.getDisplayType().equals(PostDisplayType.SINGLEPHOTO_POST))
                found.setSingleImage(this.getPostImages(found.getPostId()).get(0));
        }

        return found;
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Post> getPosts(Integer pageNumber, Integer pageSize) {
        PageRequest pageRequest =
                new PageRequest(pageNumber, pageSize, sortByPostDateDesc());
        return postRepository.findAll(pageRequest);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Post> getPublishedPosts(Integer pageNumber, Integer pageSize) {
        PageRequest pageRequest =
                new PageRequest(pageNumber, pageSize, sortByPostDateDesc());
        return postRepository.findByIsPublishedTrue(pageRequest);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Post> getAllPosts() {
        return postRepository.findAll(sortByPostDateDesc());
    }

    @Transactional(readOnly = true)
    @Override
    public List<Post> getAllPublishedPosts() {
        return postRepository.findByIsPublishedTrue(sortByPostDateDesc());
    }

    @Override
    public Optional<Post> getOneMostRecent() {
        logger.debug("Getting most recent post");
        Page<Post> posts = postRepository.findAll(new PageRequest(0, 1, sortByPostDateDesc()));
        if (posts.getContent().isEmpty()) {
            logger.debug("No documents");
            return Optional.empty();
        } else {
            Post post = posts.getContent().get(0);
            logger.trace("Returning {}", post);
            return Optional.of(post);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<Post> getPostsWithDetail() {
        return postRepository.findAllWithDetail();
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Post> getPostsByTagId(long tagId, int pageNumber, int pageSize) {
        PageRequest pageRequest =
                new PageRequest(pageNumber, pageSize, sortByPostDateDesc());
        return postRepository.findByTagId(tagId, pageRequest);
    }

    @Override
    public List<Post> getPostsByTagId(long tagId) {
        return postRepository.findAllByTagId(tagId);
    }

    //endregion

    // region Post Images

    @Transactional(readOnly = true)
    @Override
    public List<PostImage> getAllPostImages() {
        return Lists.newArrayList(postImageRepository.findAll());
    }

    @Transactional(readOnly = true)
    @Override
    public List<PostImage> getPostImages(long postId) {
        List<PostImage> images = Lists.newArrayList(postImageRepository.findByPostId(postId));
        for (PostImage image : images) {
            image.setUrl(applicationSettings.getPostImageUrlRoot());
        }
        return images;
    }

    @Transactional
    @Override
    public PostImage addImage(PostImage image) {
        return postImageRepository.save(image);
    }

    @Transactional(readOnly = true)
    @Override
    public PostImage getPostImage(long imageId) {
        return postImageRepository.findOne(imageId);
    }

    @Transactional
    @Override
    public void deleteImage(PostImage image) {
        postImageRepository.delete(image);
    }

    // endregion

    // region Tags

    @Transactional(readOnly = true)
    @Override
    public Tag getTag(String tagValue) throws TagNotFoundException {
        Tag found = tagRepository.findByTagValueIgnoreCase(tagValue);
        if (found == null) {
            logger.info("No tag found with id: {}", tagValue);
            throw new TagNotFoundException("No tag found with id: " + tagValue);
        }

        return found;
    }

    @Transactional
    private void saveNewTagsToDataBase(PostDTO postDTO) {
        for (TagDTO tagDTO : postDTO.getTags()) {
            Tag tag = tagRepository.findByTagValueIgnoreCase(tagDTO.getTagValue());
            if (tag == null) {
                tag = new Tag(tagDTO.getTagValue());
                tagRepository.save(tag);
            }
        }
    }

    @Transactional
    @Override
    public Tag createTag(TagDTO tagDTO) {
        Tag tag = tagRepository.findByTagValueIgnoreCase(tagDTO.getTagValue());
        if (tag == null) {
            tag = new Tag(tagDTO.getTagValue());
            tagRepository.save(tag);
        }
        return tag;
    }

    @Transactional
    @Override
    public Tag updateTag(TagDTO tagDTO) {
        Tag tag = tagRepository.findOne(tagDTO.getTagId());
        tag.setTagValue(tagDTO.getTagValue());
        return tag;
    }

    @Transactional
    @Override
    public void deleteTag(TagDTO tagDTO, List<Post> posts) {
        if (posts != null) {
            Tag tag = tagRepository.findOne(tagDTO.getTagId());
            for (Post post : posts) {
                post.getTags().remove(tag);
            }
        }
        tagRepository.delete(tagDTO.getTagId());
    }

    @Transactional(readOnly = true)
    @Override
    public Set<TagDTO> getTagDTOs() {
        Set<Tag> tags = tagRepository.findAll();
        return PostUtils.tagsToTagDTOs(tags);
    }

    @Transactional(readOnly = true)
    @Override
    public List<String> getTagValues() {
        Set<Tag> tags = tagRepository.findAll();
        return PostUtils.tagsToTagValues(tags);
    }


    @Transactional(readOnly = true)
    @Override
    public Set<TagDTO> getTagDTOs(Long postId) {
        return null;
    }


    @Transactional(readOnly = true)
    @Override
    public List<TagDTO> getTagCloud(int tagCount) {
        List<Tag> tagcloud = em.createNamedQuery("getTagCloud", Tag.class)
                .getResultList();
        List<TagDTO> tagDTOs = tagcloud
                .stream()
                .filter(t -> t.getPosts().size() > 0)
                .limit(tagCount)
                .map(TagDTO::new)
                .collect(Collectors.toList());
        return tagDTOs;
    }

    // endregion

    // region Posts A-Z

    @Transactional(readOnly = true)
    @Override
    public List<AlphabetDTO> getAlphaLInks() {
        char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

        // Example: 12AGHJLM
        String activeAlphas = postRepository.getAlphaLinkString();

        List<AlphabetDTO> alphaLinks = new ArrayList<>();

        // Iterate over alphabet char Array, set AlphabetDTO.active if char in activeAlphas
        for (char c : alphabet)
            alphaLinks.add(new AlphabetDTO(String.valueOf(c), activeAlphas.indexOf(c) > 0));

        // add AlphabetDTO record for "0-9", set active if any digits in activeAlphas String
        alphaLinks.add(new AlphabetDTO("0-9", activeAlphas.matches(".*\\d+.*")));

        // sort AlphabetDTO List, "0-9" followed by alphabet
        Collections.sort(alphaLinks, (o1, o2) ->
                o1.getAlphaCharacter().compareTo(o2.getAlphaCharacter()));

        // All AlphabetDTO items returned with true/false if contain links
        return alphaLinks;
    }

    @Transactional(readOnly = true)
    @Override
    public List<PostDTO> getAlphaPosts() {
        List<Post> posts = Lists.newArrayList(postRepository.findByIsPublishedTrue(sortByPostDateDesc()));

        // converting all posts to postDTO objects
        //
        // 1) post titles starting with a digit assigned "09" alphaKey
        // 2) postDTO list adds all post titles starting with letter, assigned title firstLetter as alphaKey
        // 3) for NixMash Spring Demo site, Changelists do not appear in A-Z listing

        List<PostDTO> postDTOs = posts
                .stream()
                .filter(p -> Character.isDigit(p.getPostTitle().charAt(0)))
                .map(PostDTO::buildAlphaNumericTitles)
                .sorted(byfirstLetter)
                .collect(Collectors.toList());

        postDTOs.addAll(
                posts
                        .stream()
                        .filter(p -> Character.isAlphabetic(p.getPostTitle().charAt(0)) && !p.getPostTitle().startsWith("Changelist"))
                        .map(PostDTO::buildAlphaTitles)
                        .sorted(byfirstLetter)
                        .collect(Collectors.toList()));

        return postDTOs;
    }

    private Comparator<PostDTO> byfirstLetter = (e1, e2) -> e1
            .getPostTitle().compareTo(e2.getPostTitle());

    // endregion

    // region Security Support


    @Override
    public boolean canUpdatePost(Authentication authentication, Long postId) {

        if (authentication instanceof AnonymousAuthenticationToken)
            return false;

        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();

        Post post = null;
        try {
            post = getPostById(postId);
        } catch (PostNotFoundException e) {
            logger.error("Post not found for PostId {} ", postId);
            return false;
        }

        Long postUserId = post.getUserId();
        return currentUser.getId().equals(postUserId);
    }

    // endregion

    //region Utility methods

    public Sort sortByPostDateDesc() {
        return new Sort(Sort.Direction.DESC, "postDate");
    }

    //endregion
}
