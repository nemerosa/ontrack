package net.nemerosa.ontrack.extension.github.ingestion.ui

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.github.ingestion.processing.config.IngestionConfig
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter
import org.springframework.stereotype.Component

@Component
class GQLTypeGitHubIngestionConfig : GQLType {

    override fun getTypeName(): String = "GitHubIngestionConfig"

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLBeanConverter.asObjectType(IngestionConfig::class, cache)

}