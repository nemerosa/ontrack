package net.nemerosa.ontrack.graphql.schema.links

import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLNonNull
import net.nemerosa.ontrack.graphql.schema.GQLProjectEntityFieldContributor
import net.nemerosa.ontrack.graphql.support.nullableOutputType
import net.nemerosa.ontrack.model.links.BranchLinksDirection
import net.nemerosa.ontrack.model.links.BranchLinksNode
import net.nemerosa.ontrack.model.links.BranchLinksService
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.springframework.stereotype.Component

abstract class AbstractGQLGraphFieldContributor<T : ProjectEntity>(
    private val projectEntityType: ProjectEntityType,
    private val description: String,
    private val fetcher: (T, direction: BranchLinksDirection) -> BranchLinksNode,
    private val gqlEnumBranchLinksDirection: GQLEnumBranchLinksDirection,
    private val gqlTypeBranchLinksNode: GQLTypeBranchLinksNode,
    private val branchLinksService: BranchLinksService
) : GQLProjectEntityFieldContributor {

    override fun getFields(
        projectEntityClass: Class<out ProjectEntity>,
        projectEntityType: ProjectEntityType
    ): List<GraphQLFieldDefinition>? =
        if (projectEntityType == this.projectEntityType) {
            listOf(
                GraphQLFieldDefinition.newFieldDefinition()
                    .name("graph")
                    .description(description)
                    .argument {
                        it.name(ARG_DIRECTION)
                            .description("Direction for the dependencies")
                            .type(GraphQLNonNull(gqlEnumBranchLinksDirection.getTypeRef()))
                    }
                    .type(nullableOutputType(gqlTypeBranchLinksNode.typeRef, false))
                    .dataFetcher { env ->
                        val entity: T = env.getSource()
                        val direction: BranchLinksDirection = env.getArgument<String>(ARG_DIRECTION).let {
                            BranchLinksDirection.valueOf(it)
                        }
                        fetcher(entity, direction)
                    }
                    .build()
            )
        } else {
            null
        }

    companion object {
        const val ARG_DIRECTION = "direction"
    }
}