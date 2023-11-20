package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.support.intArgument
import net.nemerosa.ontrack.graphql.support.toNotNull
import net.nemerosa.ontrack.model.structure.ID.Companion.of
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component

@Component
class GQLRootQueryBranch(
    private val structureService: StructureService,
    private val gqlTypeBranch: GQLTypeBranch,
) : GQLRootQuery {
    override fun getFieldDefinition(): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name("branch")
            .description("Getting a branch using its ID")
            .type(gqlTypeBranch.typeRef.toNotNull())
            .argument(
                intArgument("id", "ID of the branch to look for", nullable = false)
            )
            .dataFetcher { env ->
                val id: Int = env.getArgument("id")
                structureService.getBranch(of(id))
            }
            .build()

}