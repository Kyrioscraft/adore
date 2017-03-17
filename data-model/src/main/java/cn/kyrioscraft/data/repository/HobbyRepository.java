package cn.kyrioscraft.data.repository;


import cn.kyrioscraft.data.model.Hobby;
import org.springframework.data.repository.CrudRepository;

public interface HobbyRepository extends CrudRepository<Hobby, Long> {

    Hobby findByHobbyTitleIgnoreCase(String hobbyTitle);
//    Contact findByEmail(String email);
}
