package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.api.BuildDiffExtension
import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.graphql.support.GraphqlUtils
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.ui.controller.URIBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class GQLBranchDiffActionsFieldContributor
@Autowired
constructor(
        private val extensionManager: ExtensionManager,
        private val uriBuilder: URIBuilder,
        private val gqlTypeAction: GQLTypeAction
) : GQLProjectEntityFieldContributor {
    override fun getFields(projectEntityClass: Class<out ProjectEntity>, projectEntityType: ProjectEntityType): List<GraphQLFieldDefinition> {
        if (projectEntityType == ProjectEntityType.BRANCH) {
            return listOf(
                    GraphQLFieldDefinition.newFieldDefinition()
                            .name("buildDiffActions")
                            .description("Actions to get a diff on builds of the branch")
                            .type(GraphqlUtils.stdList(gqlTypeAction.type))
                            .dataFetcher(GraphqlUtils.fetcher(
                                    Branch::class.java,
                                    { branch ->
                                        extensionManager.getExtensions(BuildDiffExtension::class.java)
                                                .filter { extension -> extension.apply(branch.project) }
                                                .map { uriBuilder.resolveActionWithExtension(it, it.action) }
                                    }
                            ))
                            .build()
            )
        } else {
            return listOf()
        }
    }
}