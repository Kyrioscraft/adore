package cn.kyrioscraft.data.repository;


import cn.kyrioscraft.data.model.ContactPhone;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ContactPhoneRepository extends CrudRepository<ContactPhone, Long> {

    ContactPhone findByContactPhoneId(Long id);
    List<ContactPhone> findByContact_ContactId(Long id);

}
