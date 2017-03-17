package cn.kyrioscraft.data.service;

import cn.kyrioscraft.data.common.ApplicationSettings;
import cn.kyrioscraft.data.dto.addons.FlashcardCategoryDTO;
import cn.kyrioscraft.data.dto.addons.FlashcardDTO;
import cn.kyrioscraft.data.dto.addons.FlashcardImageDTO;
import cn.kyrioscraft.data.model.Post;
import cn.kyrioscraft.data.model.PostImage;
import cn.kyrioscraft.data.model.addons.Flashcard;
import cn.kyrioscraft.data.model.addons.FlashcardCategory;
import cn.kyrioscraft.data.repository.PostImageRepository;
import cn.kyrioscraft.data.repository.PostRepository;
import cn.kyrioscraft.data.repository.addons.FlashcardCategoryRepository;
import cn.kyrioscraft.data.repository.addons.FlashcardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created by kyrioscraft on 8/11/16.
 */
@Service("addonService")
@Transactional
public class AddonServiceImpl implements AddonService {

    private final FlashcardCategoryRepository flashcardCategoryRepository;
    private final FlashcardRepository flashcardRepository;
    private final PostRepository postRepository;
    private final ApplicationSettings applicationSettings;
    private final PostImageRepository postImageRepository;


    @Autowired
    public AddonServiceImpl(FlashcardCategoryRepository flashcardCategoryRepository, FlashcardRepository flashcardRepository, PostRepository postRepository, ApplicationSettings applicationSettings, PostImageRepository postImageRepository) {
        this.flashcardCategoryRepository = flashcardCategoryRepository;
        this.flashcardRepository = flashcardRepository;
        this.postRepository = postRepository;
        this.applicationSettings = applicationSettings;
        this.postImageRepository = postImageRepository;
    }

    @PersistenceContext
    private EntityManager em;

    // region Flashcards

    @Override
    public FlashcardCategory getFlashcardCategoryById(long categoryId) {
        return flashcardCategoryRepository.findByCategoryId(categoryId);
    }

    @Override
    public List<FlashcardCategory> getFlashcardCategories() {
        return flashcardCategoryRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteFlashcardCategory(FlashcardCategory flashcardCategory) {
        flashcardCategoryRepository.delete(flashcardCategory);
    }

    @Transactional
    @Override
    public FlashcardCategory updateFlashcardCategory(FlashcardCategoryDTO flashcardCategoryDTO) {
        FlashcardCategory flashcardCategory = flashcardCategoryRepository.findOne(flashcardCategoryDTO.getCategoryId());
        flashcardCategory.setCategory(flashcardCategoryDTO.getCategory());
        return flashcardCategory;
    }

    @Override
    public List<Flashcard> getFlashcardsByCategoryId(long categoryId) {
        return flashcardRepository.findByCategoryId(categoryId, sortByFlashcardDateDesc());
    }

    @Override
    public FlashcardCategory addFlashCardCategory(FlashcardCategory flashcardCategory) {
        return flashcardCategoryRepository.save(flashcardCategory);
    }

    @Override
    public Flashcard addFlashcard(FlashcardImageDTO flashcardImageDTO) {
        Post post = postRepository.findOne(flashcardImageDTO.getPostId());
        Flashcard flashcard = new Flashcard(flashcardImageDTO.getCategoryId(),
                                                                                                flashcardImageDTO.getImage(),
                                                                                                flashcardImageDTO.getContent(),
                                                                                                post);
        return flashcardRepository.save(flashcard);
    }

    @Override
    public List<Flashcard> getAllFlashcards() {
        return flashcardRepository.findAll();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Flashcard> getFlashcardsWithCategoryName() {
        List<Flashcard> flashcards =
                em.createNativeQuery("SELECT *  FROM v_flashcards ORDER BY datetime_created DESC", "FlashcardsWithCategory")
                .getResultList();

       return populateFlashcardImages(flashcards);
    }

    @Override
    @Transactional
    public Post getFlashcardPost(List<Flashcard> flashcards, int index) {
        Post post = flashcards.get(index).getPost();
        PostImage postImage = postImageRepository.findByPostId(post.getPostId()).get(0);
        postImage.setUrl(applicationSettings.getPostImageUrlRoot());
        post.setSingleImage(postImage);
        return post;
    }

    @Override
    @Transactional
    public Flashcard updateFlashcard(FlashcardDTO flashcardDTO) {
        Flashcard found = flashcardRepository.findOne(flashcardDTO.getSlideId());
        Post post = postRepository.findOne(flashcardDTO.getPostId());
        found.update(flashcardDTO.getCategoryId(), flashcardDTO.getContent(), post);
        em.persist(found);
        return found;
    }

    @Override
    @Transactional
    public void deleteFlashcard(Flashcard flashcard) {
        flashcardRepository.delete(flashcard);
    }

    @Override
    public List<Flashcard> getFlashcardsWithDetail() {
        return flashcardRepository.findAllWithDetail();
    }

    @Override
    public List<Flashcard> getActiveFlashcardsWithDetail(long categoryId) {
        List<Flashcard> flashcards = flashcardRepository.findActiveWithDetail(categoryId);
        return populateFlashcardImages(flashcards);
    }

    private List<Flashcard> populateFlashcardImages(List<Flashcard> flashcards) {
        String imageRootUrl = applicationSettings.getFlashcardImageUrlRoot();
        for (Flashcard flashcard : flashcards) {
            flashcard.setImageUrl(String.format("%s%s.png", imageRootUrl, flashcard.getImage()));
            flashcard.setThumbnailUrl(String.format("%s%s-thumbnail.png", imageRootUrl, flashcard.getImage()));
        }
        return flashcards;
    }

    @Override
    public List<Post> getFlashcardPosts() {
        return postRepository.findSinglePhotoPosts(sortByPostDateDesc());
    }

    // endregion

    //region Utility methods

    public Sort sortByPostDateDesc() {
        return new Sort(Sort.Direction.DESC, "postDate");
    }

    public Sort sortByFlashcardDateDesc() {
        return new Sort(Sort.Direction.DESC, "datetimeCreated");
    }

    //endregion

}
