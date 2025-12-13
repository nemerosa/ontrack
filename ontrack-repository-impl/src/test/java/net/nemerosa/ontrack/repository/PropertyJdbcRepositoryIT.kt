package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertEquals
import kotlin.test.assertNull

@Transactional
class PropertyJdbcRepositoryIT : AbstractRepositoryTestSupport() {

    @Autowired
    private lateinit var repository: PropertyRepository

    private lateinit var project: Project

    @BeforeEach
    fun create_project() {
        project = do_create_project()
    }

    @Test
    fun save_retrieve_delete_property() {
        assertNull(repository.loadProperty(PROPERTY_TYPE, ProjectEntityType.PROJECT, project.id))

        repository.saveProperty(
            PROPERTY_TYPE,
            ProjectEntityType.PROJECT,
            project.id,
            mapOf("value" to 10).asJson()
        )
        val t = repository.loadProperty(PROPERTY_TYPE, ProjectEntityType.PROJECT, project.id)
        assertEquals(
            mapOf("value" to 10).asJson(),
            t?.json
        )

        repository.deleteProperty(PROPERTY_TYPE, ProjectEntityType.PROJECT, project.id)
        assertNull(repository.loadProperty(PROPERTY_TYPE, ProjectEntityType.PROJECT, project.id))
    }

    @Test
    fun save_update_data() {
        assertNull(repository.loadProperty(PROPERTY_TYPE, ProjectEntityType.PROJECT, project.id))

        repository.saveProperty(
            PROPERTY_TYPE,
            ProjectEntityType.PROJECT,
            project.id,
            mapOf("value" to 10).asJson()
        )
        var t = repository.loadProperty(PROPERTY_TYPE, ProjectEntityType.PROJECT, project.id)
        assertEquals(
            mapOf("value" to 10).asJson(),
            t?.json
        )

        repository.saveProperty(
            PROPERTY_TYPE,
            ProjectEntityType.PROJECT,
            project.id,
            mapOf("value" to 12).asJson()
        )
        t = repository.loadProperty(PROPERTY_TYPE, ProjectEntityType.PROJECT, project.id)

        assertEquals(
            mapOf("value" to 12).asJson(),
            t?.json
        )
    }

    companion object {
        private const val PROPERTY_TYPE = "test"
    }
}
