package cn.kyrioscraft.data.repository.addons;

import cn.kyrioscraft.data.model.addons.FlashcardCategory;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by kyrioscraft on 8/11/16.
 */
public interface FlashcardCategoryRepository extends CrudRepository<FlashcardCategory, Long> {
    List<FlashcardCategory> findAll();

    FlashcardCategory findByCategoryId(long categoryId);
}
