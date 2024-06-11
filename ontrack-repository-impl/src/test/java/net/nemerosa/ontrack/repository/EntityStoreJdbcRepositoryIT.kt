package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class EntityStoreJdbcRepositoryIT : AbstractRepositoryTestSupport() {

    @Autowired
    private lateinit var repository: EntityStoreJdbcRepository

    @Test
    fun `Storing and retrieving a record`() {
        val branch = do_create_branch()
        repository.store(
            branch, STORE, Record(
                name = "my-key",
                text = "Some text",
                enabled = true,
            )
        )

        assertNotNull(repository.findByName<Record>(branch, STORE, "my-key")) {
            assertEquals("my-key", it.name)
            assertEquals("Some text", it.text)
            assertEquals(true, it.enabled)
        }

        repository.deleteByName(branch, STORE, "my-key")


        assertNull(repository.findByName<Record>(branch, STORE, "my-key"))
    }

    @Test
    fun `Deleting a complete store for an entity`() {
        val branch = do_create_branch()
        val r1 = record().apply { repository.store(branch, STORE, this) }
        val r2 = record().apply { repository.store(branch, STORE, this) }

        val branch2 = do_create_branch()
        val r3 = record().apply { repository.store(branch2, STORE, this) }

        repository.deleteByStore(branch, STORE)

        assertNull(repository.findByName(branch, STORE, r1.name))
        assertNull(repository.findByName(branch, STORE, r2.name))

        assertNotNull(repository.findByName(branch2, STORE, r3.name))
    }

    @Test
    fun `Deleting records using a filter`() {
        val branch = do_create_branch()
        val r1 = record(enabled = true).apply { repository.store(branch, STORE, this) }
        val r2 = record(enabled = false).apply { repository.store(branch, STORE, this) }

        repository.deleteByFilter(
            branch, STORE, EntityStoreFilter(
                jsonFilter = "data::jsonb->>'enabled' = :enabled",
                jsonFilterCriterias = mapOf("enabled" to "false"),
            )
        )

        assertNotNull(repository.findByName(branch, STORE, r1.name))
        assertNull(repository.findByName(branch, STORE, r2.name))
    }

    @Test
    fun `List using a filter`() {
        val branch = do_create_branch()
        val r1 = record(enabled = true).apply { repository.store(branch, STORE, this) }
        /* val r2 = */ record(enabled = false).apply { repository.store(branch, STORE, this) }

        val filter = EntityStoreFilter(
            jsonFilter = "data::jsonb->>'enabled' = :enabled",
            jsonFilterCriterias = mapOf("enabled" to "true"),
        )

        assertEquals(
            1,
            repository.getCountByFilter(branch, STORE, filter)
        )

        assertEquals(
            listOf(r1),
            repository.getByFilter(branch, STORE, filter)
        )
    }

    @Test
    fun `Iterating over records`() {
        val branch = do_create_branch()
        val r1 = record(enabled = true).apply { repository.store(branch, STORE, this) }
        /* val r2 = */ record(enabled = false).apply { repository.store(branch, STORE, this) }

        val filter = EntityStoreFilter(
            jsonFilter = "data::jsonb->>'enabled' = :enabled",
            jsonFilterCriterias = mapOf("enabled" to "true"),
        )

        val names = mutableListOf<String>()

        repository.forEachByFilter<Record>(branch, STORE, filter) {
            names.add(it.name)
        }

        assertEquals(
            listOf(r1.name),
            names
        )
    }

    @Test
    fun `Deleting a complete store for all entities`() {
        val branches = (1..5).map { do_create_branch() }
        branches.forEach { branch ->
            repeat(5) {
                record().apply { repository.store(branch, STORE, this) }
            }
        }

        branches.forEach { branch ->
            assertEquals(
                5,
                repository.getCountByFilter(branch, STORE, EntityStoreFilter())
            )
        }

        repository.deleteByStoreForAllEntities(STORE)

        branches.forEach { branch ->
            assertEquals(
                0,
                repository.getCountByFilter(branch, STORE, EntityStoreFilter())
            )
        }
    }

    companion object {
        private const val STORE = "testing_store"

        private fun record(
            enabled: Boolean = true,
        ) = Record(
            name = uid("r"),
            text = uid("Some text for "),
            enabled = enabled,
        )
    }

    data class Record(
        override val name: String,
        val text: String,
        val enabled: Boolean,
    ) : EntityStoreRecord

}