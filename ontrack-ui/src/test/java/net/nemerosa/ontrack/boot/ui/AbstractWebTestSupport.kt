package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.boot.Application
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.it.AsAdminTest
import org.springframework.boot.test.context.SpringBootTest

/**
 * See also [net.nemerosa.ontrack.boot.support.UITest]
 */
@SpringBootTest(classes = [Application::class])
@AsAdminTest
abstract class AbstractWebTestSupport : AbstractDSLTestSupport()
