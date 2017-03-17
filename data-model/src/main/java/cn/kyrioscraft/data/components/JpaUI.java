package cn.kyrioscraft.data.components;

import cn.kyrioscraft.data.common.ApplicationSettings;
import cn.kyrioscraft.data.common.ISiteOption;
import cn.kyrioscraft.data.common.SiteOptions;
import cn.kyrioscraft.data.dto.AlphabetDTO;
import cn.kyrioscraft.data.dto.PostDTO;
import cn.kyrioscraft.data.dto.SiteOptionDTO;
import cn.kyrioscraft.data.enums.PostDisplayType;
import cn.kyrioscraft.data.enums.PostType;
import cn.kyrioscraft.data.exceptions.ContactNotFoundException;
import cn.kyrioscraft.data.exceptions.DuplicatePostNameException;
import cn.kyrioscraft.data.exceptions.SiteOptionNotFoundException;
import cn.kyrioscraft.data.model.UserConnection;
import cn.kyrioscraft.data.model.addons.Flashcard;
import cn.kyrioscraft.data.model.addons.FlashcardCategory;
import cn.kyrioscraft.data.service.*;
import cn.kyrioscraft.data.utils.ContactUtils;
import cn.kyrioscraft.data.utils.PostUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JpaUI {

    // region  Beans

    private final PostService postService;
    private final ContactService contactService;
    private final UserService userService;
    private final SiteService siteService;
    private final ApplicationSettings applicationSettings;
    final DefaultListableBeanFactory beanfactory;
    private final SiteOptions siteOptions;
    private final AddonService addonService;

    // endregion

    @Autowired
    public JpaUI(ContactService contactService, PostService postService, SiteOptions siteOptions, UserService userService, ApplicationSettings applicationSettings, DefaultListableBeanFactory beanfactory, SiteService siteService, AddonService addonService) {
        this.contactService = contactService;
        this.postService = postService;
        this.siteOptions = siteOptions;
        this.userService = userService;
        this.applicationSettings = applicationSettings;
        this.beanfactory = beanfactory;
        this.siteService = siteService;
        this.addonService = addonService;
    }

    public void init() {
        displayFlashcards();
    }

    private void displayFlashcards() {

        List<FlashcardCategory> flashcardCategories = addonService.getFlashcardCategories();
        for (FlashcardCategory flashcardCategory : flashcardCategories) {
            System.out.println(addonService.getFlashcardsByCategoryId(flashcardCategory.getCategoryId()));
        }


        System.out.println("\nFor Category One -------------------------------------- */\n ");

        List<Flashcard> flashcards = addonService.getFlashcardsByCategoryId(1L);
        for (Flashcard flashcard : flashcards) {
            System.out.println(flashcard);
        }

        System.out.println("\nWith Category Name -------------------------------------- */\n ");

        List<Flashcard> flashcardsAndCategories = addonService.getFlashcardsWithCategoryName();
        for (Flashcard flashcard : flashcardsAndCategories) {
            System.out.println(flashcard);
        }

    }

    private void generateAlphabet() {

        List<AlphabetDTO> alphaLinks = postService.getAlphaLInks();
        for (AlphabetDTO alphaLink : alphaLinks) {
            System.out.println(alphaLink.getAlphaCharacter() + " " + alphaLink.getActive());
        }
    }

    private void displayRandomUserIdString() {
        System.out.println(RandomStringUtils.randomAlphanumeric(16));
    }

    private void addPostDemo() throws DuplicatePostNameException {
        String title = "Best way to create SEO friendly URI string";
        PostDTO postDTO = PostDTO.getBuilder(
                1L,
                title,
                PostUtils.createSlug(title),
                "http://nixmash.com/java/variations-on-json-key-value-pairs-in-spring-mvc/",
                "This is the post content",
                PostType.LINK,
                PostDisplayType.LINK_FEATURE
        ).build();
        postService.add(postDTO);
    }

    // region Unused demos

    private void siteOptionsDemo() {
        System.out.println("Initialized SiteOptions Bean Property: " +
                siteOptions.getGoogleAnalyticsTrackingId());

        Boolean reset = true;
        String siteName = reset ? "My Site" : "My Updated Site Name";
        String integerProperty = reset ? "1" : "8";

        try {
            siteService.update(new SiteOptionDTO(ISiteOption.SITE_NAME, siteName));
            siteService.update(new SiteOptionDTO(ISiteOption.INTEGER_PROPERTY, integerProperty));
        } catch (SiteOptionNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("New SiteOptions values: " + siteOptions.getSiteName() + " -- " + siteOptions.getIntegerProperty());
        System.out.println("GoogleAnalyticsId: " + siteOptions.getGoogleAnalyticsTrackingId());
    }

    public void entityDemo() {

        UserConnection userConnection = userService.getUserConnectionByUserId("daver");
        ContactUtils.listUserConnection("My User Connection", userConnection);
        ContactUtils.listUsersWithDetail(userService.getUsersByAuthorityId(1L));
        ContactUtils.listUser("USER BY EMAIL",
                userService.getByEmail("user@aol.com").get());
        try {
            ContactUtils.listContact("CONTACT BY EMAIL",
                    contactService.findContactById(1L));
        } catch (ContactNotFoundException e) {
            e.printStackTrace();
        }

        ContactUtils.listContacts("ENTITIES FIND ALL", contactService.findAll());
        ContactUtils.listContacts("ENTITIES FIND BY FIRST NAME",
                contactService.findByFirstName("Barry"));
        ContactUtils.listContacts("ENTITIES FIND BY FIRST AND LAST NAME",
                contactService.findByFirstNameAndLastName("Tad", "Grant"));

        ContactUtils.listContact("SINGLE CONTACT: ",
                contactService.getContactByEmail("Nam.nulla@pedenonummyut.edu"));
        ContactUtils.listContactsWithDetail(contactService.getContactsWithDetail());

        ContactUtils.listContactWithDetail(contactService.getContactByIdWithDetail(2L));

        ContactUtils.listContacts("FIND BY FIRST NAME",
                contactService.findByFirstName("Summer"));

        ContactUtils.listContactWithDetail(contactService.getContactByIdWithDetail(1L));
        ContactUtils.contactToContactDTO(contactService.getContactByIdWithDetail(2L));

        try {
            contactService.update(ContactUtils.contactToContactDTO(contactService.getContactByIdWithDetail(2L)));
        } catch (ContactNotFoundException e) {
            e.printStackTrace();
        }
    }

    // endregion

}
