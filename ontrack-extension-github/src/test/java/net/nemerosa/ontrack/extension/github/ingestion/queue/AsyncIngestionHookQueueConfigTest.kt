package net.nemerosa.ontrack.extension.github.ingestion.queue

import net.nemerosa.ontrack.extension.github.ingestion.IngestionConfigProperties
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Owner
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Repository
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class AsyncIngestionHookQueueConfigTest {

    private lateinit var ingestionConfigProperties: IngestionConfigProperties

    @BeforeEach
    fun before() {
        ingestionConfigProperties = IngestionConfigProperties()
    }

    @Test
    fun `Computing the routing key for a default queue without scaling`() {
        val repository = repository()
        val key = AsyncIngestionHookQueueConfig.getRoutingKey(ingestionConfigProperties, repository)
        assertEquals("default", key)
    }

    @Test
    fun `Computing the routing key for a default queue with scaling`() {
        val repository = repository(name = "ontrack", login = "test")
        ingestionConfigProperties.processing.scale = 10
        val key = AsyncIngestionHookQueueConfig.getRoutingKey(ingestionConfigProperties, repository)
        assertEquals("default.9", key)
    }

    @Test
    fun `Computing the routing key for a specific queue without scaling`() {
        val repository = repository()
        ingestionConfigProperties.processing.repositories = mapOf(
            "specific" to IngestionConfigProperties.RepositoryQueueConfig(
                owner = repository.owner.login,
            )
        )
        val key = AsyncIngestionHookQueueConfig.getRoutingKey(ingestionConfigProperties, repository)
        assertEquals("specific", key)
    }

    @Test
    fun `Computing the routing key for a specific queue with scaling`() {
        val repository = repository()
        ingestionConfigProperties.processing.scale = 10
        ingestionConfigProperties.processing.repositories = mapOf(
            "specific" to IngestionConfigProperties.RepositoryQueueConfig(
                owner = repository.owner.login,
            )
        )
        val key = AsyncIngestionHookQueueConfig.getRoutingKey(ingestionConfigProperties, repository)
        assertEquals("specific", key)
    }

    @Test
    fun `Computing the routing key for a non matching specific queue without scaling`() {
        val repository = repository()
        ingestionConfigProperties.processing.repositories = mapOf(
            "specific" to IngestionConfigProperties.RepositoryQueueConfig(
                owner = "other",
            )
        )
        val key = AsyncIngestionHookQueueConfig.getRoutingKey(ingestionConfigProperties, repository)
        assertEquals("default", key)
    }

    @Test
    fun `Computing the routing key for a non matching specific queue with scaling`() {
        val repository = repository(name = "ontrack", login = "test")
        ingestionConfigProperties.processing.scale = 10
        ingestionConfigProperties.processing.repositories = mapOf(
            "specific" to IngestionConfigProperties.RepositoryQueueConfig(
                owner = "other",
            )
        )
        val key = AsyncIngestionHookQueueConfig.getRoutingKey(ingestionConfigProperties, repository)
        assertEquals("default.9", key)
    }

    @Test
    fun `Computing the routing key for a null repository without scaling`() {
        ingestionConfigProperties.processing.repositories = mapOf(
            "specific" to IngestionConfigProperties.RepositoryQueueConfig(
                owner = "other",
            )
        )
        val key = AsyncIngestionHookQueueConfig.getRoutingKey(ingestionConfigProperties, null)
        assertEquals("default", key)
    }

    @Test
    fun `Computing the routing key for a null repository with scaling`() {
        ingestionConfigProperties.processing.scale = 10
        ingestionConfigProperties.processing.repositories = mapOf(
            "specific" to IngestionConfigProperties.RepositoryQueueConfig(
                owner = "other",
            )
        )
        val key = AsyncIngestionHookQueueConfig.getRoutingKey(ingestionConfigProperties, null)
        assertEquals("default.0", key)
    }

    private fun repository(
        name: String = uid("r"),
        login: String = uid("u"),
    ) = Repository(
        name = name,
        description = null,
        owner = Owner(login = login),
        htmlUrl = "uri",
    )

}