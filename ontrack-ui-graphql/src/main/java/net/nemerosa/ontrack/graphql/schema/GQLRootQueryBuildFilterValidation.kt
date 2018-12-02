package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars.GraphQLInt
import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.support.GraphqlUtils
import net.nemerosa.ontrack.model.buildfilter.BuildFilterService
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component

/**
 * Query to validate a build filter.
 */
@Component
class GQLRootQueryBuildFilterValidation(
        private val inputBuildGenericFilter: GQLInputBuildGenericFilter,
        private val structureService: StructureService,
        private val buildFilterService: BuildFilterService
) : GQLRootQuery {
    override fun getFieldDefinition(): GraphQLFieldDefinition {
        return GraphQLFieldDefinition.newFieldDefinition()
                .name("buildFilterValidation")
                .argument {
                    it.name("branchId")
                            .description("ID of the branch to look for")
                            .type(GraphQLNonNull(GraphQLInt))
                }
                .argument {
                    it.name("filter")
                            .description("Generic filter based on a configured filter")
                            .type(GraphQLNonNull(inputBuildGenericFilter.typeRef))
                }
                .type(
                        GraphQLObjectType.newObject()
                                .name("BuildFilterValidation")
                                .description("Build filter validation result.")
                                .field {
                                    it.name("error")
                                            .description("Validation message or null if valid.")
                                            .type(GraphQLString)
                                }
                                .build()
                )
                .dataFetcher { environment ->
                    val branchId = GraphqlUtils.getIntArgument(environment, "branchId")
                            .orElseThrow { IllegalStateException("From argument is required") }
                    val branch = structureService.getBranch(ID.of(branchId))
                    val genericFilter: Any? = environment.getArgument<Any>("generic")
                    val filterType = inputBuildGenericFilter.getFilterType(genericFilter)
                    val filterData = inputBuildGenericFilter.getFilterData(genericFilter)
                    mapOf(
                            "error" to buildFilterService.validateBuildFilterProviderData(
                                    branch,
                                    filterType,
                                    filterData
                            )
                    )
                }
                .build()
    }
}