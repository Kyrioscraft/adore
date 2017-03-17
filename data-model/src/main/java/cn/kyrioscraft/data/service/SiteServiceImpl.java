package cn.kyrioscraft.data.service;

import cn.kyrioscraft.data.common.SiteOptions;
import cn.kyrioscraft.data.dto.SiteOptionDTO;
import cn.kyrioscraft.data.exceptions.SiteOptionNotFoundException;
import cn.kyrioscraft.data.model.SiteOption;
import cn.kyrioscraft.data.repository.SiteOptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;

@Service("siteService")
@Transactional
public class SiteServiceImpl implements SiteService{

    private static final Logger logger = LoggerFactory.getLogger(SiteServiceImpl.class);

    private SiteOptionRepository siteOptionRepository;
    private SiteOptions siteOptions;

    @Autowired
    public SiteServiceImpl(SiteOptionRepository siteOptionRepository, SiteOptions siteOptions) {
        this.siteOptionRepository = siteOptionRepository;
        this.siteOptions = siteOptions;
    }

    @Override
    public SiteOption update(SiteOptionDTO siteOptionDTO) throws SiteOptionNotFoundException {
        logger.info("Updating siteOption property {} with value: {}",
                siteOptionDTO.getName(), siteOptionDTO.getValue());

        SiteOption found = findOptionByName(siteOptionDTO.getName());
        found.update(siteOptionDTO.getName(), siteOptionDTO.getValue());

        try {
            siteOptions.setSiteOptionProperty(siteOptionDTO.getName(), siteOptionDTO.getValue());
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            logger.error("Error updating SiteOption Properties " + e.getMessage());
        }
        return found;
    }

    @Transactional(readOnly = true)
    @Override
    public SiteOption findOptionByName(String name) throws SiteOptionNotFoundException {

        logger.info("Finding siteOption property with name: {}", name);
        SiteOption found = siteOptionRepository.findByNameIgnoreCase(name);

        if (found == null) {
            logger.info("No siteOption property with name: {}", name);
            throw new SiteOptionNotFoundException("No siteOption with property name: " + name);
        }

        return found;
    }

}



