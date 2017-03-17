package cn.kyrioscraft.data;

import cn.kyrioscraft.data.components.JpaUI;
import cn.kyrioscraft.data.config.ApplicationConfig;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.SpringVersion;
import org.springframework.core.env.Environment;

/**
 * Created by kyrioscraft on 2017/3/2.
 */
@SpringBootApplication
public class JpaLauncher {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(ApplicationConfig.class);
        ctx.refresh();
        Environment env = ctx.getEnvironment();
        boolean contain = env.containsProperty("spring.profiles.active");
        System.out.println(contain+"");
        System.out.println("Spring Framework Version: " + SpringVersion.getVersion());
        System.out.println("Spring Boot Version: " + SpringBootVersion.getVersion());
        JpaUI ui = ctx.getBean(JpaUI.class);
        ui.init();
        ctx.close();
    }
}
