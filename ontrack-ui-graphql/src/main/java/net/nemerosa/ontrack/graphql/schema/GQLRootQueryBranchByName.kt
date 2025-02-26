package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.support.stringArgument
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@Component
class GQLRootQueryBranchByName(
    private val structureService: StructureService,
    private val gqlTypeBranch: GQLTypeBranch,
) : GQLRootQuery {

    override fun getFieldDefinition(): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name("branchByName")
            .description("Getting a branch using its name")
            .type(gqlTypeBranch.typeRef)
            .argument(stringArgument(ARG_PROJECT, "Name of the project"))
            .argument(stringArgument(ARG_BRANCH, "Name of the branch"))
            .dataFetcher { env ->
                val projectName: String = env.getArgument(ARG_PROJECT)
                val branchName: String = env.getArgument(ARG_BRANCH)
                structureService.findBranchByName(projectName, branchName).getOrNull()
            }
            .build()

    companion object {
        private const val ARG_PROJECT = "project"
        private const val ARG_BRANCH = "name"
    }

}