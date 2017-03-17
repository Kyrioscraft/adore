package cn.kyrioscraft.data.config;

import cn.kyrioscraft.data.model.auditors.CurrentTimeDateTimeService;
import cn.kyrioscraft.data.model.auditors.DateTimeService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Created with IntelliJ IDEA.
 * User: kyrioscraft
 * Date: 5/7/15
 * Time: 4:20 PM
 */
@Configuration
@EnableConfigurationProperties
@EnableTransactionManagement
@ComponentScan(basePackages = "cn.kyrioscraft.data")
@EnableJpaRepositories(basePackages = "cn.kyrioscraft.data")
@PropertySource("classpath:/spring/application.properties")
public class ApplicationConfig {

    @Bean
    DateTimeService currentTimeDateTimeService() {
        return new CurrentTimeDateTimeService();
    }

}
