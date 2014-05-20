package net.nemerosa.ontrack.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.MultiPartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.servlet.MultipartConfigElement;

@Configuration
@ComponentScan("net.nemerosa.ontrack")
@EnableAutoConfiguration
public class Application {

    // TODO Removes this in Spring Boot 1.1.x
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultiPartConfigFactory factory = new MultiPartConfigFactory();
        factory.setMaxFileSize("1Mb");
        factory.setMaxRequestSize("1Mb");
        return factory.createMultipartConfig();
    }

    /**
     * Start-up point
     *
     * @param args Arguments passed to the program, they may contain configuration variables.
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
