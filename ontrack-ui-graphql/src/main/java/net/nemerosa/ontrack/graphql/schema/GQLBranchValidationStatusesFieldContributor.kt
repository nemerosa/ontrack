package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLList
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@Component
class GQLBranchValidationStatusesFieldContributor(
    private val structureService: StructureService,
) : GQLProjectEntityFieldContributor {

    override fun getFields(
        projectEntityClass: Class<out ProjectEntity>,
        projectEntityType: ProjectEntityType
    ): List<GraphQLFieldDefinition>? =
        if (projectEntityType == ProjectEntityType.BRANCH) {
            listOf(
                GraphQLFieldDefinition.newFieldDefinition()
                    .name("validationStatuses")
                    .description("Given a list of validation names, returns for each one the last validation run or null if the validation does not exist.")
                    .argument {
                        it.name("names")
                            .type(GraphQLNonNull(GraphQLList(GraphQLNonNull(GraphQLString))))
                    }
                    .type(listType(GraphQLTypeReference(GQLTypeValidationRun.VALIDATION_RUN)))
                    .dataFetcher { env ->
                        val branch: Branch = env.getSource()!!
                        val names: List<String> = env.getArgument("names") ?: emptyList()
                        val runs = mutableListOf<ValidationRun>()
                        names.forEach { name ->
                            val vs = structureService.findValidationStampByName(branch.project.name, branch.name, name)
                                .getOrNull()
                            if (vs != null) {
                                val run =
                                    structureService.getValidationRunsForValidationStamp(vs, offset = 0, count = 1)
                                        .firstOrNull()
                                if (run != null) {
                                    runs += run
                                }
                            }
                        }
                        // OK
                        runs
                    }
                    .build()
            )
        } else {
            null
        }
}