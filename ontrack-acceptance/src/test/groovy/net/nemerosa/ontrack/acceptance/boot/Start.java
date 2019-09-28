package net.nemerosa.ontrack.acceptance.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Collection;

@SpringBootApplication(scanBasePackages = "net.nemerosa.ontrack.acceptance")
public class Start {

    public static void main(String... args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(Start.class, args);
        Collection<AcceptanceRunner> runners = ctx.getBeansOfType(AcceptanceRunner.class).values();
        boolean allOK = runners.stream().allMatch(Start::runner);
        if (allOK) {
            System.exit(0);
        } else {
            System.exit(1);
        }
    }

    private static boolean runner(AcceptanceRunner it) {
        try {
            return it.run();
        } catch (Exception e) {
            throw new RuntimeException("Cannot run acceptance runner", e);
        }
    }

}
