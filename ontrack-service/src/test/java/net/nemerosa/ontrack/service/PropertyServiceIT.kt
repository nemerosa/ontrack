package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.extension.api.support.TestSimpleProperty
import net.nemerosa.ontrack.extension.api.support.TestSimplePropertyType
import net.nemerosa.ontrack.it.AbstractDSLTestJUnit4Support
import net.nemerosa.ontrack.model.security.ProjectEdit
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PropertyServiceIT : AbstractDSLTestJUnit4Support() {

    @Test
    fun `Has property`() {
        project {
            assertFalse(propertyService.hasProperty(this, TestSimplePropertyType::class.java), "No property set yet")
            // Setting the property
            asUser().with(this, ProjectEdit::class.java).call {
                propertyService.editProperty(this, TestSimplePropertyType::class.java, TestSimpleProperty("my-value"))
                assertTrue(propertyService.hasProperty(this, TestSimplePropertyType::class.java), "Property is now set")
            }
        }
    }

}