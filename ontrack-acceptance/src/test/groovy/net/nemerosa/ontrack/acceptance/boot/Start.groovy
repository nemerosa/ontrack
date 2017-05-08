package net.nemerosa.ontrack.acceptance.boot

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication(scanBasePackages = 'net.nemerosa.ontrack.acceptance')
class Start {

    static void main(String... args) {
        def ctx = SpringApplication.run(Start.class, args);
        def runners = ctx.getBeansOfType(AcceptanceRunner).values()
        boolean allOK = runners.collect { it -> it.run() }.every()
        if (allOK) {
            System.exit 0
        } else {
            System.exit 1
        }
    }

}
