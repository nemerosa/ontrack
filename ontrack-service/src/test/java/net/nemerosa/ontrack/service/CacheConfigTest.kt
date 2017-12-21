package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.extension.api.support.TestProperty
import net.nemerosa.ontrack.extension.api.support.TestPropertyType
import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.repository.PropertyRepository
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertTrue

class CacheConfigTest : AbstractServiceTestSupport() {

    @Autowired
    private lateinit var propertyRepository: PropertyRepository

    @Test
    fun `Properties are cached`() {
        val project = doCreateProject()
        setProperty(
                project,
                TestPropertyType::class.java,
                TestProperty.of("test")
        )
        // Gets the raw property
        val p = propertyRepository.loadProperty(
                TestPropertyType::class.qualifiedName,
                project.projectEntityType,
                project.id
        )
        // Gets it a second time
        val p2 = propertyRepository.loadProperty(
                TestPropertyType::class.qualifiedName,
                project.projectEntityType,
                project.id
        )
        // Checks this is the same instance
        assertTrue(p === p2, "Cache enabled")
    }

}