package net.nemerosa.ontrack.service.labels

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.it.AsAdminTest
import net.nemerosa.ontrack.model.labels.*
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@AsAdminTest
class LabelProviderServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var labelProviderService: LabelProviderService

    @Autowired
    private lateinit var testCountLabelProvider: TestCountLabelProvider

    @Autowired
    private lateinit var testCustomLabelProvider: TestCustomLabelProvider

    @BeforeEach
    fun setup() {
        testCountLabelProvider.reset()
        testCustomLabelProvider.reset()
    }

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
    fun `Provided labels updates at project level`() {
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
            assertEquals(
                    1,
                    labels.filter { it.computedBy?.name == "Name label" }.size
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
            assertEquals(
                    1,
                    labels.filter { it.computedBy?.name == "Name label" }.size
            )
        }
    }

    @Test
    fun `Provided labels updates at global level`() {
        // Enabling the custom labelling (with default description)
        val name = uid("N")
        testCustomLabelProvider.labelName = name
        project {
            // Collects of labels
            collectLabels()
            // Checks the provided custom labels
            val label = labels.find {
                it.category == "custom" && it.name == name
            }
            assertNotNull(label) {
                // Default description
                assertEquals("Custom label for custom:$name", it.description)
            }
            // Changing the description
            testCustomLabelProvider.labelDescription = { _, _ -> "Test description" }
            // Recollecting the labels
            collectLabels()
            // Checks the provided custom labels
            val newLabel = labels.find {
                it.category == "custom" && it.name == name
            }
            assertNotNull(newLabel) {
                // Default description
                assertEquals("Test description", it.description)
            }
        }
    }

    @Test
    fun `Overriding manual labels with automated ones without any existing association`() {
        // Creating a manual label (and random name)
        val manualLabel = label(category = "custom")
        // Not computed
        assertNull(manualLabel.computedBy, "Manual label is not computed")
        // Configures the custom provider so that it provides the name cat/name
        testCustomLabelProvider.labelName = manualLabel.name
        // For a given project
        project {
            // Auto collection
            collectLabels()
            // Gets the custom labels for this project
            val labels = this.labels.filter { it.category == "custom" }
            // Checks that we have 1 label and that it is provided
            assertEquals(1, labels.size)
            val label = labels[0]
            assertEquals(manualLabel.name, label.name)
            assertEquals(TestCustomLabelProvider::class.qualifiedName, label.computedBy?.id)
        }
        // Checks that the manual label is now computed
        val label = labelManagementService.getLabel(manualLabel.id)
        assertEquals(manualLabel.name, label.name)
        assertEquals(TestCustomLabelProvider::class.qualifiedName, label.computedBy?.id)
    }

    @Test
    fun `Overriding manual labels with automated ones with existing association`() {
        // Creating a manual label (and random name)
        val manualLabel = label(category = "custom")
        // Not computed
        assertNull(manualLabel.computedBy, "Manual label is not computed")
        // Configures the custom provider so that it provides the name cat/name
        testCustomLabelProvider.labelName = manualLabel.name
        // For a given project
        project {
            // Associates the manual label to this project
            labels += manualLabel
            // Auto collection
            collectLabels()
            // Gets the custom labels for this project
            val labels = this.labels.filter { it.category == "custom" }
            // Checks that we have 1 label and that it is provided
            assertEquals(1, labels.size)
            val label = labels[0]
            assertEquals(manualLabel.name, label.name)
            assertEquals(TestCustomLabelProvider::class.qualifiedName, label.computedBy?.id)
        }
        // Checks that the manual label is now computed
        val label = labelManagementService.getLabel(manualLabel.id)
        assertEquals(manualLabel.name, label.name)
        assertEquals(TestCustomLabelProvider::class.qualifiedName, label.computedBy?.id)
    }

    @Test
    fun `Providers can steal other providers labels`() {
        // Configuring the custom provider so that it uses the same cat/name than the count provider
        testCustomLabelProvider.labelCustom = "count"
        testCustomLabelProvider.labelName = "1"
        // Project to test with
        project {
            // Collecting the labels
            collectLabels()
            // Only 1 label with category = count
            val labels = this.labels.filter { it.category == "count" }
            assertEquals(1, labels.size)
            val label = labels[0]
            assertEquals("count", label.category)
            assertEquals("1", label.name)
            assertNotNull(label.computedBy, "Label is computed")
        }
    }

    @Test
    fun `Trying to create a manual label over an automated one is not allowed`() {
        project {
            collectLabels()
            // Assert the automated label is created
            val createdLabel = asAdmin().call {
                labelManagementService.labels.find {
                    it.category == "count"
                }
            }
            assertNotNull(createdLabel) {
                assertEquals("1", it.name)
                assertNotNull(it.computedBy)
            }
            // Attempting to create manual similar label
            assertFailsWith<LabelCategoryNameAlreadyExistException> {
                label(category = "count", name = "1", checkForExisting = false)
            }
        }
    }

    @Test
    fun `Trying to update a manual label over an automated one is not allowed`() {
        project {
            collectLabels()
            // Assert the automated label is created
            val createdLabel = asAdmin().call {
                labelManagementService.labels.find {
                    it.category == "count"
                }
            }
            assertNotNull(createdLabel) {
                assertEquals("1", it.name)
                assertNotNull(it.computedBy)
            }
            // Attempting to update manual similar label
            assertFailsWith<LabelNotEditableException> {
                asAdmin().execute {
                    labelManagementService.updateLabel(
                            createdLabel.id,
                            LabelForm(
                                    category = "count",
                                    name = "1",
                                    description = "Just a test",
                                    color = "#FF0000"
                            )
                    )
                }
            }
        }
    }

    @Test
    fun `Trying to delete a manual label over an automated one is not allowed`() {
        project {
            collectLabels()
            // Assert the automated label is created
            val createdLabel = asAdmin().call {
                labelManagementService.labels.find {
                    it.category == "count"
                }
            }
            assertNotNull(createdLabel) {
                assertEquals("1", it.name)
                assertNotNull(it.computedBy)
            }
            // Attempting to update manual similar label
            assertFailsWith<LabelNotEditableException> {
                asAdmin().execute {
                    labelManagementService.deleteLabel(
                            createdLabel.id
                    )
                }
            }
        }
    }

    @Configuration
    class LabelProviderServiceITConfig {

        @Bean
        fun testCountLabelProvider(): TestLabelProvider = TestCountLabelProvider()

        @Bean
        fun testAbbrevLabelProvider(): TestLabelProvider = TestAbbrevLabelProvider()

        @Bean
        fun testCustomLabelProvider(): TestLabelProvider = TestCustomLabelProvider()

    }

    private fun Project.collectLabels() {
        asAdmin().execute {
            labelProviderService.collectLabels(this)
        }
    }

}

abstract class TestLabelProvider : LabelProvider {
    override val isEnabled: Boolean = true
}

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

    fun reset() {
        range = 1..1
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

class TestCustomLabelProvider : TestLabelProvider() {

    var labelCustom: String? = "custom"
    var labelName: String? = null
    var labelDescription: (category: String?, name: String) -> String = { category, name -> "Custom label for $category:$name" }

    override val name: String = "Custom label"

    fun reset() {
        labelCustom = "custom"
        labelName = null
        labelDescription = { category, name -> "Custom label for $category:$name" }
    }

    override fun getLabelsForProject(project: Project): List<LabelForm> {
        return labelName?.let {
            listOf(
                    LabelForm(
                            category = labelCustom,
                            name = it,
                            description = labelDescription(labelCustom, it),
                            color = "#0000FF"
                    )
            )
        } ?: emptyList()
    }

}