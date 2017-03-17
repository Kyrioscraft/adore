package cn.kyrioscraft.web;

import cn.kyrioscraft.web.common.Application;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

/**
 * Created by kyrioscraft on 2017/3/3.
 */
public class WebInitializer extends SpringBootServletInitializer{

    @Override
    protected SpringApplicationBuilder configure (SpringApplicationBuilder application){
        return application.sources(Application.class);
    }
}
