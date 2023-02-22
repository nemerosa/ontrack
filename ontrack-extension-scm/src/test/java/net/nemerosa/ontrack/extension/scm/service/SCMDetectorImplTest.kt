package net.nemerosa.ontrack.extension.scm.service

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.model.structure.ProjectFixtures
import org.junit.jupiter.api.Test
import kotlin.test.assertIs
import kotlin.test.assertNull

class SCMDetectorImplTest {

    @Test
    fun no_result_when_no_provider() {
        val extensionManager = mockk<ExtensionManager>()
        val detector = SCMDetectorImpl(extensionManager)

        every { extensionManager.getExtensions(SCMExtension::class.java) } returns emptyList()

        val scm = detector.getSCM(ProjectFixtures.testProject())
        assertNull(scm, "No SCM detected")
    }

    @Test
    fun provider() {
        val extensionManager = mockk<ExtensionManager>()
        val detector = SCMDetectorImpl(extensionManager)

        every { extensionManager.getExtensions(SCMExtension::class.java) } returns listOf(TestSCMExtension.instance)
        val project = ProjectFixtures.testProject()
        TestSCMExtension.instance.registerProjectForTestSCM(project) {}

        val scm = detector.getSCM(project)
        assertIs<TestSCMExtension.TestSCM>(scm)
    }
}