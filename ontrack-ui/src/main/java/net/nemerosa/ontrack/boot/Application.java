package net.nemerosa.ontrack.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.system.ApplicationPidFileWriter;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.File;

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

        // PID file
        File pid = new File("ontrack.pid");

        // Runs the application
        SpringApplication application = new SpringApplication(Application.class);
        application.addListeners(new ApplicationPidFileWriter(pid));
        application.run(args);
    }

}
