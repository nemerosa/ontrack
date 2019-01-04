package net.nemerosa.ontrack.service.labels

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.labels.LabelForm
import net.nemerosa.ontrack.model.labels.LabelProvider
import net.nemerosa.ontrack.model.labels.LabelProviderService
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.annotation.PostConstruct
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class LabelProviderServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var labelProviderService: LabelProviderService

    @Autowired
    private lateinit var testCountLabelProvider: TestCountLabelProvider

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

    @Test
    fun `Provided labels updates`() {
        project {
            // Configuration of the labels
            testCountLabelProvider.range = 1..3
            // Collects of labels
            collectLabels()
            // Checks the provided labels
            assertEquals(
                    listOf("1", "2", "3"),
                    labels.filter { it.category == "count" }.map { it.name }
            )
            // Reconfiguration of the labels
            testCountLabelProvider.range = 2..5
            // Collects of labels
            collectLabels()
            // Checks the provided labels
            assertEquals(
                    listOf("2", "3", "4", "5"),
                    labels.filter { it.category == "count" }.map { it.name }
            )
        }
    }

    @Configuration
    class LabelProviderServiceITConfig(
            private val ontrackConfigProperties: OntrackConfigProperties
    ) {

        @Bean
        fun testCountLabelProvider(): TestLabelProvider = TestCountLabelProvider()

        @Bean
        fun testAbbrevLabelProvider(): TestLabelProvider = TestAbbrevLabelProvider()

        @PostConstruct
        fun disabling_label_provider_job() {
            ontrackConfigProperties.isJobLabelProviderEnabled = false
        }

    }

    private fun Project.collectLabels() {
        asAdmin().execute {
            labelProviderService.collectLabels(this)
        }
    }

}

abstract class TestLabelProvider : LabelProvider

class TestCountLabelProvider : TestLabelProvider() {

    var range: IntRange = 1..1

    override val name: String = "Count label"

    override fun getLabelsForProject(project: Project): List<LabelForm> {
        return range.map { count ->
            LabelForm(
                    category = "count",
                    name = count.toString(),
                    description = "Count $count",
                    color = "#00FF00"
            )
        }
    }

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