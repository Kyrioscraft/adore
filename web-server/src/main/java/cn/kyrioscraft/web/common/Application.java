package cn.kyrioscraft.web.common;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.velocity.VelocityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Created by kyrioscraft on 2017/3/3.
 */
@EnableTransactionManagement
@EnableConfigurationProperties
@ComponentScan(basePackages = "cn.kyrioscraft")
@EnableJpaRepositories(basePackages = "cn.kyrioscraft.data")
@EnableAutoConfiguration(exclude = {VelocityAutoConfiguration.class})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
