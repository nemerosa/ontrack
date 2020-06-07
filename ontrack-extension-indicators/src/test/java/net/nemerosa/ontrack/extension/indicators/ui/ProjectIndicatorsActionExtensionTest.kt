package net.nemerosa.ontrack.extension.indicators.ui

import net.nemerosa.ontrack.extension.indicators.IndicatorsTestFixtures
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.support.ActionType
import net.nemerosa.ontrack.test.assertNotPresent
import net.nemerosa.ontrack.test.assertPresent
import org.junit.Test
import kotlin.test.assertEquals

class ProjectIndicatorsActionExtensionTest {

    private val extension = ProjectIndicatorsActionExtension(IndicatorsTestFixtures.indicatorsExtensionFeature())

    @Test
    fun `For a project`() {
        val project = Project.of(NameDescription.nd("P", "")).withId(ID.of(1))
        val action = extension.getAction(project)
        assertPresent(action) {
            assertEquals("project-indicators/1", it.uri)
            assertEquals(ActionType.LINK, it.type)
        }
    }

    @Test
    fun `Not for a project`() {
        val project = Project.of(NameDescription.nd("P", "")).withId(ID.of(1))
        val branch = Branch.of(project, NameDescription.nd("B", "")).withId(ID.of(10))
        val action = extension.getAction(branch)
        assertNotPresent(action)
    }

}