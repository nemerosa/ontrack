package net.nemerosa.ontrack.acceptance.boot

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan('net.nemerosa.ontrack.acceptance.boot')
class Start {

    @Autowired
    private AcceptanceConfig config

    static void main(String... args) {
        def ctx = SpringApplication.run(Start.class, args);
        def runners = ctx.getBeansOfType(AcceptanceRunner).values()
        runners.each { it -> it.run() }
        /*

        JUnitCore junit = new JUnitCore()
        // TODO Filtering on tests
        junit.run(
                ACCBrowserBasic,
//                ACCSearch,
//                ACCStructure
        )
        */
    }

}
