package net.nemerosa.ontrack.docs

import net.nemerosa.ontrack.model.annotations.getAPITypeDescription
import net.nemerosa.ontrack.model.docs.getFieldsForDocumentationClass
import net.nemerosa.ontrack.model.structure.PropertyType
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.findAnnotations
import kotlin.test.fail

/**
 * Generation of the documentation for all properties.
 */
class PropertiesDocumentationIT : AbstractDocGenIT() {

    @Autowired
    private lateinit var propertyTypes: List<PropertyType<*>>

    @Test
    fun `Properties documentation`() {
        docGenSupport.inDirectory("properties") {

            val prodProperties = propertyTypes.filter { it.isNotTest() }

            writeFile(
                fileName = "index",
            ) { s ->
                s.title("List of properties for Yontrack.")
                for (propertyType in prodProperties) {
                    s.tocItem(propertyType.fileTitle, fileName = "${propertyType.fileId}.md")
                }
            }

            prodProperties.forEach { propertyType ->
                generateProperty(this, propertyType)
            }
        }
    }

    private fun generateProperty(directoryContext: DocGenDirectoryContext, propertyType: PropertyType<*>) {
        val description = getPropertyDescription(propertyType)

        val fileId = propertyType.fileId

        val propertyClass = propertyType::class.supertypes.firstOrNull()?.arguments?.firstOrNull()?.type?.classifier
        val parameters = if (propertyClass != null && propertyClass is KClass<*>) {
            getFieldsForDocumentationClass(propertyClass)
        } else {
            fail("Cannot find property type for $propertyType")
        }

        directoryContext.writeFile(
            fileId = fileId,
            title = propertyType.fileTitle,
            header = description,
            fields = parameters,
            example = null,
            links = propertyType::class.findAnnotations(),
            linksPrefix = "../../",
        )
    }

    private val PropertyType<*>.id: String get() = this::class.java.name
    private val PropertyType<*>.fileId: String get() = "property-${this.id}"
    private val PropertyType<*>.fileTitle: String get() = this.name

    private fun getPropertyDescription(propertyType: PropertyType<*>): String {
        val s = StringBuilder()

        propertyType::class.findAnnotation<Deprecated>()?.let {
            s.append("Deprecated: ").append(it.message).append("\n\n")
        }

        s.append("ID: `").append(propertyType.id).append("`\n\n")

        val methodDescription = propertyType.description
        if (methodDescription.isNotBlank()) {
            s.append(methodDescription).append("\n\n")
        }

        val typeDescription = getAPITypeDescription(propertyType::class)
        if (!typeDescription.isNullOrBlank()) {
            s.append(typeDescription).append("\n\n")
        }

        val scope = propertyType.supportedEntityTypes
        s.h2("Scope")
        for (type in scope) {
            s.append("* ").append(type.displayName).append("\n")
        }

        return s.toString()
    }

    private fun PropertyType<*>.isNotTest(): Boolean {
        return !this::class.java.simpleName.startsWith("Test") &&
                !this::class.java.simpleName.startsWith("Mock")
    }

}