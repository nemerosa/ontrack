package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.model.buildfilter.BuildFilterService
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.springframework.stereotype.Component

@Component
class GQLBranchBuildFilterFieldsContributor(
        private val gqlTypeBuildFilterForm: GQLTypeBuildFilterForm,
        private val gqlTypeBuildFilterResource: GQLTypeBuildFilterResource,
        private val buildFilterService: BuildFilterService,
) : GQLProjectEntityFieldContributor {
    override fun getFields(projectEntityClass: Class<out ProjectEntity>, projectEntityType: ProjectEntityType): List<GraphQLFieldDefinition>? =
            if (projectEntityType == ProjectEntityType.BRANCH) {
                listOf(
                        GraphQLFieldDefinition.newFieldDefinition()
                                .name("buildFilterForms")
                                .description("List of forms for the build filters (for the creation of new filters)")
                                .type(listType(gqlTypeBuildFilterForm.typeRef))
                                .dataFetcher { env ->
                                    val branch: Branch = env.getSource()!!
                                    buildFilterService.getBuildFilterForms(branch.id)
                                }
                                .build(),
                        GraphQLFieldDefinition.newFieldDefinition()
                                .name("buildFilterResources")
                                .description("List of shared build filters for this branch")
                                .type(listType(gqlTypeBuildFilterResource.typeRef))
                                .dataFetcher { env ->
                                    val branch: Branch = env.getSource()!!
                                    buildFilterService.getBuildFilters(branch.id)
                                }
                                .build(),
                )
            } else {
                null
            }
}