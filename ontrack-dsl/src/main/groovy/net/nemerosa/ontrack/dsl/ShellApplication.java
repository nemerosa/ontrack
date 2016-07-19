package net.nemerosa.ontrack.dsl;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ShellApplication {

    @Bean
    public Shell shell() {
        return Shell.forCmdLine();
    }

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(ShellApplication.class);
        application.setBannerMode(Banner.Mode.OFF);
        ConfigurableApplicationContext context = application.run(args);
        context.getBeansOfType(Shell.class).get("shell").call(args);
    }

}
