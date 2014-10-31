package net.nemerosa.ontrack.boot.ui

import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class APIControllerIT extends AbstractWebTestSupport {

    @Autowired
    private APIController apiController

    @Test
    void 'Mappings'() {
        def resources = apiController.show()
        resources.resources.each { apiInfo ->
            println "* API: ${apiInfo.name}"
            apiInfo.methods.each { apiMethodInfo ->
                println "\t* ${apiMethodInfo.name} @ ${apiMethodInfo.path} ${apiMethodInfo.methods}"
            }
        }
    }

}
