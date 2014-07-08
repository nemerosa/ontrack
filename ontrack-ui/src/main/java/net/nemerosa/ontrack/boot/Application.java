package net.nemerosa.ontrack.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("net.nemerosa.ontrack")
@EnableAutoConfiguration
public class Application {

    /**
     * Start-up point
     *
     * @param args Arguments passed to the program, they may contain configuration variables.
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
