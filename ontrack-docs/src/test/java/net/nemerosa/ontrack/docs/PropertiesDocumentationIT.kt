package net.nemerosa.ontrack.docs

import net.nemerosa.ontrack.model.structure.PropertyType
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

/**
 * Generation of the documentation for all properties.
 */
class PropertiesDocumentationIT : AbstractDocGenIT() {

    @Autowired
    private lateinit var propertyTypes: List<PropertyType<*>>

    @Test
    fun `Properties documentation`() {
        docGenSupport.inDirectory("properties") {

//            val prodProperties = propertyTypes.filter { it.isNotTest() }

//            writeIndex(
//                fileId = "appendix-properties-index",
//                level = 3,
//                title = "List of properties",
//                items = prodProperties.associate { propertyType ->
//                    getPropertyFileId(propertyType) to getPropertyTitle(propertyType)
//                }
//            )
//
//            prodProperties.forEach { propertyType ->
//                generateProperty(this, propertyType)
//            }
        }
    }

}