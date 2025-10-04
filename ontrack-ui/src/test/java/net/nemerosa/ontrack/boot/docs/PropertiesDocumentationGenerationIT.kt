package net.nemerosa.ontrack.boot.docs

import net.nemerosa.ontrack.it.AbstractITTestSupport.AbstractIntegrationTestConfiguration
import net.nemerosa.ontrack.model.annotations.getAPITypeDescription
import net.nemerosa.ontrack.model.docs.getFieldsForDocumentationClass
import net.nemerosa.ontrack.model.structure.PropertyType
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.findAnnotations
import kotlin.test.fail

/**
 * Generation of the documentation
 */
@Disabled("To be launched manually when need be")
@SpringBootTest(
    classes = [
        AbstractIntegrationTestConfiguration::class
    ],
    webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
class PropertiesDocumentationGenerationIT : AbstractDocumentationGenerationTestSupport() {

    @Autowired
    private lateinit var propertyTypes: List<PropertyType<*>>

    @Test
    fun `Properties documentation generation`() {
        withDirectory("properties") {

            writeIndex(
                fileId = "appendix-properties-index",
                level = 3,
                title = "List of properties",
                items = propertyTypes.filter { it.isNotTest() }.associate { propertyType ->
                    getPropertyFileId(propertyType) to getPropertyTitle(propertyType)
                }
            )

            propertyTypes.filter { it.isNotTest() }.forEach { propertyType ->
                generateProperty(this, propertyType)
            }

        }
    }

    private fun PropertyType<*>.isNotTest(): Boolean {
        return !this::class.java.simpleName.startsWith("Test") &&
                !this::class.java.simpleName.startsWith("Mock")
    }

    private val PropertyType<*>.id: String get() = this::class.java.name

    private fun getPropertyFileId(propertyType: PropertyType<*>): String =
        "property-${propertyType.id}"

    private fun getPropertyTitle(propertyType: PropertyType<*>): String = propertyType.name

    private fun generateProperty(directoryContext: DirectoryContext, propertyType: PropertyType<*>) {
        val description = getPropertyDescription(propertyType)

        val fileId = getPropertyFileId(propertyType)

        val propertyClass = propertyType::class.supertypes.firstOrNull()?.arguments?.firstOrNull()?.type?.classifier
        val parameters = if (propertyClass != null && propertyClass is KClass<*>) {
            getFieldsForDocumentationClass(propertyClass)
        } else {
            fail("Cannot find property type for $propertyType")
        }

        directoryContext.writeFile(
            fileId = fileId,
            level = 4,
            title = getPropertyTitle(propertyType),
            header = description,
            fields = parameters,
            example = null,
            links = propertyType::class.findAnnotations(),
        )
    }

    private fun getPropertyDescription(propertyType: PropertyType<*>): String? {
        val s = StringBuilder()

        propertyType::class.findAnnotation<Deprecated>()?.let {
            s.append("Deprecated: ").append(it.message).append("\n\n")
        }

        s.append("ID: `").append(propertyType.id).append("`\n\n")

        val methodDescription = propertyType.description
        if (!methodDescription.isNullOrBlank()) {
            s.append(methodDescription).append("\n\n")
        }

        val typeDescription = getAPITypeDescription(propertyType::class)
        if (!typeDescription.isNullOrBlank()) {
            s.append(typeDescription).append("\n\n")
        }

        val scope = propertyType.supportedEntityTypes
        s.append("Scope:\n\n")
        for (type in scope) {
            s.append("* ").append(type.displayName).append("\n")
        }

        return s.toString()
    }

}