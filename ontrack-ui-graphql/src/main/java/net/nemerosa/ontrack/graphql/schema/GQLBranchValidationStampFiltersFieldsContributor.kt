package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.support.booleanArgument
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.model.buildfilter.BuildFilterService
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.ValidationStampFilterService
import org.springframework.stereotype.Component

@Component
class GQLBranchValidationStampFiltersFieldsContributor(
        private val gqlTypeValidationStampFilter: GQLTypeValidationStampFilter,
        private val validationStampFilterService: ValidationStampFilterService,
) : GQLProjectEntityFieldContributor {
    override fun getFields(projectEntityClass: Class<out ProjectEntity>, projectEntityType: ProjectEntityType): List<GraphQLFieldDefinition>? =
            if (projectEntityType == ProjectEntityType.BRANCH) {
                listOf(
                        GraphQLFieldDefinition.newFieldDefinition()
                                .name("validationStampFilters")
                                .description("List of existing validation stamp filters for this branch.")
                                .argument(
                                        booleanArgument(ARG_ALL, "If all project & global filters must be included as well.")
                                )
                                .type(listType(gqlTypeValidationStampFilter.typeRef))
                                .dataFetcher { env ->
                                    val branch: Branch = env.getSource()!!
                                    val all = env.getArgument<Boolean>(ARG_ALL) ?: true
                                    validationStampFilterService.getBranchValidationStampFilters(branch, all)
                                }
                                .build(),
                )
            } else {
                null
            }

    companion object {
        const val ARG_ALL = "all"
    }
}