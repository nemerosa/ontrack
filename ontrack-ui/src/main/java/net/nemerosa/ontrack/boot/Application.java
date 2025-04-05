package net.nemerosa.ontrack.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import java.io.File;

@SpringBootApplication(scanBasePackages = "net.nemerosa.ontrack")
@EnableWebSecurity
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
