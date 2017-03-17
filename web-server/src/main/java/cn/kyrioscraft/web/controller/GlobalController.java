package cn.kyrioscraft.web.controller;

import cn.kyrioscraft.data.common.ApplicationSettings;
import cn.kyrioscraft.data.common.SiteOptions;
import cn.kyrioscraft.data.exceptions.ContactNotFoundException;
import cn.kyrioscraft.data.exceptions.ResourceNotFoundException;
import cn.kyrioscraft.data.model.CurrentUser;
import cn.kyrioscraft.web.components.WebUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by kyrioscraft on 2017/3/7.
 */
@ControllerAdvice
public class GlobalController {
    private static final Logger logger = LoggerFactory.getLogger(GlobalController.class);
    protected static final String ERROR_CUSTOM_VIEW="errors/custom";
    private static final String PRODUCT_MAP_VIEW="products/map";
    private static final String LOCATION_ERROR_MESSAGE_KEY="product.map.page.feedback.error";
    public static final String LOCATION_ERROR_ATTRIBUT="mappingError";
    public static final String SESSION_USER_CONNECTION="MY_USER_CONNECTION";

    @Autowired
    WebUI webUI;

    @Autowired
    private ApplicationSettings applicationSettings;

    @Autowired
    private SiteOptions siteOptions;

    @ModelAttribute("currentUser")
    public CurrentUser getCurrentUser(Authentication authentication){
        CurrentUser currentUser = null;
        if(authentication == null)
            return null;
        else
            currentUser = (CurrentUser) authentication.getPrincipal();

        return currentUser;
    }

    @ModelAttribute("isDemoUser")
    public Boolean isDemoUser(Authentication authentication) {
        Boolean isDemoUser = false;
        if (authentication != null) {
            if (authentication.getName().equals("user")) {
                isDemoUser = true;
            }
        }
        return isDemoUser;
    }

    @ModelAttribute("appSettings")
    public ApplicationSettings getApplicationSettings() {
        return applicationSettings;
    }

    @ModelAttribute("siteOptions")
    public SiteOptions getSiteOptions() {
        return siteOptions;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleResourceNotFoundException() {
        return "errors/404";
    }

    @ExceptionHandler(ContactNotFoundException.class)
    public ModelAndView handleContactNotFoundException() {
        logger.info("In ContactNotFound Exception Handler");

        ModelAndView mav = new ModelAndView();
        mav.addObject("errortitle", "Contact Missing in Action!");
        mav.addObject("errorbody", "We'll find the rascal, don't you worry");
        mav.setViewName(ERROR_CUSTOM_VIEW);
        return mav;
    }

}
