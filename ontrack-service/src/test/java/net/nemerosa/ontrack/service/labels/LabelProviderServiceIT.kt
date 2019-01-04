package net.nemerosa.ontrack.service.labels

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.labels.LabelForm
import net.nemerosa.ontrack.model.labels.LabelProvider
import net.nemerosa.ontrack.model.labels.LabelProviderService
import net.nemerosa.ontrack.model.structure.Project
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class LabelProviderServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var labelProviderService: LabelProviderService

    @Test
    fun `Provided labels for a project`() {
        project {
            collectLabels()
            // Checks that labels have been provided
            val labelCategory = name.substring(0..1)
            val labelName = name.substring(0..2)
            val label = labels.find {
                it.category == labelCategory && it.name == labelName
            }
            assertNotNull(label) {
                assertEquals(
                        TestAbbrevLabelProvider::class.qualifiedName,
                        it.computedBy?.id
                )
                assertEquals(
                        "Name label",
                        it.computedBy?.name
                )
            }
        }
    }

    @Configuration
    class LabelProviderServiceITConfig {

        @Bean
        fun testAbbrevLabelProvider(): TestLabelProvider = TestAbbrevLabelProvider()

    }

    private fun Project.collectLabels() {
        asAdmin().execute {
            labelProviderService.collectLabels(this)
        }
    }

}

abstract class TestLabelProvider : LabelProvider {

}

class TestAbbrevLabelProvider : TestLabelProvider() {

    override val name: String = "Name label"

    override fun getLabelsForProject(project: Project): List<LabelForm> {
        return listOf(
                LabelForm(
                        category = project.name.substring(0..1),
                        name = project.name.substring(0..2),
                        description = "Abbreviation for ${project.name.substring(0..2)}",
                        color = "#FF0000"
                )
        )
    }

}