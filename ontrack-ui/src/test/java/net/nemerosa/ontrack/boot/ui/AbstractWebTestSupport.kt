package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.boot.Application
import net.nemerosa.ontrack.it.AbstractDSLTestJUnit4Support
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [Application::class])
abstract class AbstractWebTestSupport : AbstractDSLTestJUnit4Support()
