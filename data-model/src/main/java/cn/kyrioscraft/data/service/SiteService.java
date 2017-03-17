package cn.kyrioscraft.data.service;

import cn.kyrioscraft.data.dto.SiteOptionDTO;
import cn.kyrioscraft.data.exceptions.SiteOptionNotFoundException;
import cn.kyrioscraft.data.model.SiteOption;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by kyrioscraft on 5/7/16.
 */
public interface SiteService {

    SiteOption update(SiteOptionDTO siteOptionDTO) throws SiteOptionNotFoundException;

    @Transactional(readOnly = true)
    SiteOption findOptionByName(String name) throws SiteOptionNotFoundException;

}
