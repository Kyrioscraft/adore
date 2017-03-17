package cn.kyrioscraft.data.repository;


import cn.kyrioscraft.data.model.Tag;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface TagRepository extends CrudRepository<Tag, Long> {

    Tag findByTagValueIgnoreCase(String tagValue);

    Set<Tag> findAll();
}
