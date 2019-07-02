package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.extension.api.support.TestExtensionFeature
import net.nemerosa.ontrack.extension.api.support.TestPropertyType
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventCannotRenderEntityException
import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.structure.NameDescription.nd
import net.nemerosa.ontrack.model.support.NameValue
import net.nemerosa.ontrack.service.events.EventFactoryImpl
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class EventFactoryImplTest {

    private val factory = EventFactoryImpl()

    private val testPropertyType = TestPropertyType(TestExtensionFeature())

    @Test
    fun newProject() {
        val e = factory.newProject(project())
        assertNull(e.signature)
        assertEquals(e.entities.size, 1)
        assertEquals("New project P.", e.renderText())
        assertEquals("""New project <a href="#/project/1">P</a>.""", e.render(testRenderer))
    }

    @Test
    fun deleteProject() {
        val e = factory.deleteProject(project())
        assertNull(e.signature)
        assertEquals(e.entities.size, 0)
        assertEquals("Project P has been deleted.", e.renderText())
        assertEquals("""Project <i class="project">P</i> has been deleted.""", e.render(testRenderer))
    }

    @Test
    fun newBuild() {
        val e = factory.newBuild(build())
        assertEquals("user", e.signature.user.name)
        assertEquals(e.entities.size, 3)
        assertEquals("New build 1 for branch B in P.", e.renderText())
        assertEquals("""New build <a href="#/build/100">1</a> for branch <a href="#/branch/10">B</a> in <a href="#/project/1">P</a>.""", e.render(testRenderer))
    }

    @Test
    fun updateBuild() {
        val e = factory.updateBuild(build())
        assertNull(e.signature)
        assertEquals(e.entities.size, 3)
        assertEquals("Build 1 for branch B in P has been updated.", e.renderText())
    }

    @Test
    fun deleteBuild() {
        val e = factory.deleteBuild(build())
        assertNull(e.signature)
        assertEquals(e.entities.size, 2)
        assertEquals("Build 1 for branch B in P has been deleted.", e.renderText())
    }

    @Test
    fun deletePromotionLevel() {
        val e = factory.deletePromotionLevel(promotionLevel())
        assertNull(e.signature)
        assertEquals(e.entities.size, 2)
        assertEquals("Promotion level COPPER for branch B in P has been deleted.", e.renderText())
    }

    @Test
    fun reorderPromotionLevels() {
        val e = factory.reorderPromotionLevels(branch())
        assertNull(e.signature)
        assertEquals(e.entities.size, 2)
        assertEquals("Promotion levels for branch B in P have been reordered.", e.renderText())
    }

    @Test
    fun imageValidationStamp() {
        val e = factory.imageValidationStamp(validationStamp())
        assertNull(e.signature)
        assertEquals(e.entities.size, 3)
        assertEquals("Image for validation stamp SMOKE for branch B in P has changed.", e.renderText())
    }

    @Test
    fun updateValidationStamp() {
        val e = factory.updateValidationStamp(validationStamp())
        assertNull(e.signature)
        assertEquals(e.entities.size, 3)
        assertEquals("Validation stamp SMOKE for branch B in P has been updated.", e.renderText())
    }

    @Test
    fun deleteValidationStamp() {
        val e = factory.deleteValidationStamp(validationStamp())
        assertNull(e.signature)
        assertEquals(e.entities.size, 2)
        assertEquals("Validation stamp SMOKE for branch B in P has been deleted.", e.renderText())
    }

    @Test
    fun reorderValidationStamps() {
        val e = factory.reorderValidationStamps(branch())
        assertNull(e.signature)
        assertEquals(e.entities.size, 2)
        assertEquals("Validation stamps for branch B in P have been reordered.", e.renderText())
    }

    @Test
    fun newPromotionRun() {
        val e = factory.newPromotionRun(promotionRun())
        assertEquals("user", e.signature.user.name)
        assertEntities(
                e,
                ProjectEntityType.PROJECT,
                ProjectEntityType.BRANCH,
                ProjectEntityType.BUILD,
                ProjectEntityType.PROMOTION_LEVEL,
                ProjectEntityType.PROMOTION_RUN
        )
        assertEquals("Build 1 has been promoted to COPPER for branch B in P.", e.renderText())
        assertEquals("""Build <a href="#/build/100">1</a> has been promoted to <a href="#/promotionLevel/100">COPPER</a> for branch <a href="#/branch/10">B</a> in <a href="#/project/1">P</a>.""", e.render(testRenderer))
    }

    @Test
    fun deletePromotionRun() {
        val e = factory.deletePromotionRun(promotionRun())
        assertNull(e.signature)
        assertEntities(
                e,
                ProjectEntityType.PROJECT,
                ProjectEntityType.BRANCH,
                ProjectEntityType.BUILD,
                ProjectEntityType.PROMOTION_LEVEL,
                ProjectEntityType.PROMOTION_RUN
        )
        assertEquals("Promotion COPPER of build 1 has been deleted for branch B in P.", e.renderText())
    }

    @Test
    fun newValidationRun() {
        val e = factory.newValidationRun(validationRun())
        assertEquals("user", e.signature.user.name)
        assertEquals(e.entities.size, 5)
        assertEquals("Build 1 has run for SMOKE with status Failed in branch B in P.", e.renderText())
        assertEquals("""Build <a href="#/build/100">1</a> has run for <a href="#/validationStamp/100">SMOKE</a> with status <i class="status">Failed</i> in branch <a href="#/branch/10">B</a> in <a href="#/project/1">P</a>.""", e.render(testRenderer))
    }

    @Test
    fun newValidationRunStatus() {
        val e = factory.newValidationRunStatus(validationRun())
        assertEquals("user", e.signature.user.name)
        assertEquals(e.entities.size, 5)
        assertEquals("Status for SMOKE validation #1 for build 1 in branch B of P has changed to Failed.", e.renderText())
        assertEquals("""Status for <a href="#/validationStamp/100">SMOKE</a> validation <a href="#/validationRun/1000">#1</a> for build <a href="#/build/100">1</a> in branch <a href="#/branch/10">B</a> of <a href="#/project/1">P</a> has changed to <i class="status">Failed</i>.""", e.render(testRenderer))
    }

    @Test
    fun propertyChange_on_promotion_level() {
        val e = factory.propertyChange(promotionLevel(), testPropertyType)
        assertNull(e.signature)
        assertEquals(e.entities.size, 2)
        assertEquals("Configuration value property has changed for promotion level COPPER.", e.renderText())
    }

    @Test
    fun propertyDelete_on_promotion_level() {
        val e = factory.propertyDelete(promotionLevel(), testPropertyType)
        assertNull(e.signature)
        assertEquals(e.entities.size, 2)
        assertEquals("Configuration value property has been removed from promotion level COPPER.", e.renderText())
    }

    @Test
    fun propertyChange_on_project() {
        val e = factory.propertyChange(project(), testPropertyType)
        assertNull(e.signature)
        assertEquals(e.entities.size, 1)
        assertEquals("Configuration value property has changed for project P.", e.renderText())
    }

    @Test
    fun propertyDelete_on_project() {
        val e = factory.propertyDelete(project(), testPropertyType)
        assertNull(e.signature)
        assertEquals(e.entities.size, 1)
        assertEquals("Configuration value property has been removed from project P.", e.renderText())
    }

    private fun promotionRun(): PromotionRun {
        val branch = branch()
        return PromotionRun.of(
                Build.of(branch, nd("1", "Build"), Signature.of("user")).withId(ID.of(100)),
                PromotionLevel.of(branch, nd("COPPER", "")).withId(ID.of(100)),
                Signature.of("user"),
                ""
        ).withId(ID.of(1000))
    }

    private fun promotionLevel(): PromotionLevel = PromotionLevel.of(branch(), nd("COPPER", "")).withId(ID.of(100))

    private fun validationRun(): ValidationRun {
        val branch = branch()
        return ValidationRun.of(
                Build.of(branch, nd("1", "Build"), Signature.of("user")).withId(ID.of(100)),
                ValidationStamp.of(branch, nd("SMOKE", "")).withId(ID.of(100)),
                1,
                Signature.of("user"),
                ValidationRunStatusID.STATUS_FAILED,
                ""
        ).withId(ID.of(1000))
    }

    private fun validationStamp(): ValidationStamp = ValidationStamp.of(branch(), nd("SMOKE", "")).withId(ID.of(100))

    private fun build(): Build = Build.of(branch(), nd("1", "Build"), Signature.of("user")).withId(ID.of(100))

    private fun branch(): Branch = Branch.of(project(), nd("B", "Branch")).withId(ID.of(10))

    private fun project(): Project = Project.of(nd("P", "Project")).withId(ID.of(1))

    private val testRenderer: EventRenderer = object : EventRenderer {

        override fun render(e: ProjectEntity, event: Event): String {
            return when (e) {
                is Project -> link(e.name, "project/${e.id}")
                is Branch -> link(e.name, "branch/${e.id}")
                is PromotionLevel -> link(e.name, "promotionLevel/${e.id}")
                is ValidationStamp -> link(e.name, "validationStamp/${e.id}")
                is Build -> link(e.name, "build/${e.id}")
                is ValidationRun -> link("#${e.runOrder}", "validationRun/${e.id}")
                else -> throw EventCannotRenderEntityException(e.entityDisplayName, e)
            }
        }

        override fun render(valueKey: String, value: NameValue, event: Event): String =
                """<i class="$valueKey">${value.value}</i>"""

        private fun link(name: String, uri: String): String = """<a href="#/$uri">$name</a>"""
    }

    private fun assertEntities(e: Event, vararg types: ProjectEntityType) {
        assertEquals(
                types.toSet(),
                e.entities.map { it.key }.toSet()
        )
        assertEquals(
                types.toSet(),
                e.entities.map { it.value.projectEntityType }.toSet()
        )
    }

}
