package net.nemerosa.ontrack.docs

import net.nemerosa.ontrack.boot.Application
import net.nemerosa.ontrack.it.AbstractITTestSupport
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

/**
 * Generation of the documentation.
 */
@SpringBootTest(classes = [Application::class])
class AbstractDocGenIT : AbstractITTestSupport() {

    @Autowired
    protected lateinit var docGenSupport: DocGenSupport

}