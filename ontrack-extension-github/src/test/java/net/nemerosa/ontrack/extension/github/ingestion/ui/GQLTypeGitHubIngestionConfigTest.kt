package net.nemerosa.ontrack.extension.github.ingestion.ui

import net.nemerosa.ontrack.extension.github.ingestion.processing.config.IngestionConfig
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter
import org.junit.Test
import kotlin.test.assertEquals

class GQLTypeGitHubIngestionConfigTest {

    @Test
    fun `Programmatic type creation`() {
        val type = GraphQLBeanConverter.asObjectType(IngestionConfig::class, GQLTypeCache())
        assertEquals("GitHubIngestionConfig", type.name)
    }
}