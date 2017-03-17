package cn.kyrioscraft.data.model.validators;

import cn.kyrioscraft.data.model.Contact;
import cn.kyrioscraft.data.model.ContactPhone;
import cn.kyrioscraft.data.service.ContactService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ContactFormValidator implements Validator {

    private static final Logger logger = LoggerFactory.getLogger(ContactFormValidator.class);
    private final ContactService contactService;

    @Autowired
    public ContactFormValidator(ContactService contactService) {
        this.contactService = contactService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(Contact.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        logger.debug("Validating {}", target);
        Contact form = (Contact) target;
        if (!form.isNew()) {
            validateContactPhones(errors, form);
            validateHobbies(errors, form);
        }
    }

    private void validateContactPhones(Errors errors, Contact form) {
        for (ContactPhone contactPhone : form.getContactPhones())
            if (contactPhone.getPhoneNumber().isEmpty() ||
                    contactPhone.getPhoneType().isEmpty()) {
                errors.reject("NotEmpty.contact.contactPhone.field");
                break;
            }
    }

    private void validateHobbies(Errors errors, Contact form) {
        if (form.getHobbies() == null)
            errors.reject("NotEmpty.contact.hobbies");
    }

}
