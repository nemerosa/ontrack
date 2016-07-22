package net.nemerosa.ontrack.dsl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ShellApplication {

    private final Logger logger = LoggerFactory.getLogger(ShellApplication.class);

    @Bean
    public Shell shell() {
        return Shell.forCmdLine(new OntrackLogger() {
            @Override
            public void trace(String message) {
                logger.debug(message);
            }
        });
    }

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(ShellApplication.class);
        application.setBannerMode(Banner.Mode.OFF);
        ConfigurableApplicationContext context = application.run(args);
        context.getBeansOfType(Shell.class).get("shell").call(args);
    }

}
