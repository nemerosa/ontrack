package net.nemerosa.ontrack.boot.ui

import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class APIControllerIT extends AbstractWebTestSupport {

    @Autowired
    private APIController apiController

    @Test
    void 'Mappings'() {
        apiController.show()
    }

}
