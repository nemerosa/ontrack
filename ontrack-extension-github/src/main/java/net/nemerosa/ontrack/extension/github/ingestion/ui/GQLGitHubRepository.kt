package net.nemerosa.ontrack.extension.github.ingestion.ui

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Repository
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.getDescription
import net.nemerosa.ontrack.graphql.support.stringField
import net.nemerosa.ontrack.graphql.support.toNotNull
import org.springframework.stereotype.Component

@Component
class GQLGitHubRepository(
    private val gqlGitHubOwner: GQLGitHubOwner,
) : GQLType {

    override fun getTypeName(): String = "GitHubRepository"

    override fun createType(cache: GQLTypeCache): GraphQLObjectType = GraphQLObjectType.newObject()
        .name(typeName)
        .description("GitHub repository")
        .stringField(
            Repository::name.name,
            getDescription(Repository::name),
        )
        .stringField(
            Repository::htmlUrl.name,
            getDescription(Repository::htmlUrl),
        )
        .field {
            it.name(Repository::owner.name)
                .description(getDescription(Repository::owner))
                .type(gqlGitHubOwner.typeRef.toNotNull())
        }
        .build()
}