package cn.kyrioscraft.data.service;

import cn.kyrioscraft.data.dto.addons.FlashcardCategoryDTO;
import cn.kyrioscraft.data.dto.addons.FlashcardDTO;
import cn.kyrioscraft.data.dto.addons.FlashcardImageDTO;
import cn.kyrioscraft.data.model.Post;
import cn.kyrioscraft.data.model.addons.Flashcard;
import cn.kyrioscraft.data.model.addons.FlashcardCategory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by kyrioscraft on 8/11/16.
 */
public interface AddonService {
    FlashcardCategory getFlashcardCategoryById(long categoryId);

    List<FlashcardCategory> getFlashcardCategories();

    @Transactional
    void deleteFlashcardCategory(FlashcardCategory flashcardCategory);

    @Transactional
    FlashcardCategory updateFlashcardCategory(FlashcardCategoryDTO flashcardCategoryDTO);

    List<Flashcard> getFlashcardsByCategoryId(long categoryId);

    FlashcardCategory addFlashCardCategory(FlashcardCategory flashcardCategory);

    Flashcard addFlashcard(FlashcardImageDTO flashcardImageDTO);

    List<Flashcard> getAllFlashcards();

    List<Flashcard> getFlashcardsWithCategoryName();

    @Transactional
    Post getFlashcardPost(List<Flashcard> flashcards, int index);

    Flashcard updateFlashcard(FlashcardDTO flashcardDTO);

    void deleteFlashcard(Flashcard flashcard);

    List<Flashcard> getFlashcardsWithDetail();

    List<Flashcard> getActiveFlashcardsWithDetail(long categoryId);

    List<Post> getFlashcardPosts();
}
