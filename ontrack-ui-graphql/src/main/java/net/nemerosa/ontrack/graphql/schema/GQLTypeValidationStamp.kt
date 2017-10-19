package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars
import graphql.schema.DataFetcher
import graphql.schema.GraphQLFieldDefinition.newFieldDefinition
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLObjectType.newObject
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.graphql.support.GraphqlUtils
import net.nemerosa.ontrack.model.structure.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
class GQLTypeValidationStamp @Autowired
constructor(private val structureService: StructureService,
            creation: GQLTypeCreation,
            private val validationRun: GQLTypeValidationRun,
            projectEntityFieldContributors: List<GQLProjectEntityFieldContributor>) : AbstractGQLProjectEntity<ValidationStamp>(ValidationStamp::class.java, ProjectEntityType.VALIDATION_STAMP, projectEntityFieldContributors, creation) {

    override fun getType(): GraphQLObjectType {
        return newObject()
                .name(VALIDATION_STAMP)
                .withInterface(projectEntityInterface())
                .fields(projectEntityInterfaceFields())
                // Image flag
                .field { f ->
                    f.name("image")
                            .description("Flag to indicate if an image is associated")
                            .type(Scalars.GraphQLBoolean)
                }
                // Ref to branch
                .field(
                        newFieldDefinition()
                                .name("branch")
                                .description("Reference to branch")
                                .type(GraphQLTypeReference(GQLTypeBranch.BRANCH))
                                .build()
                )
                // Validation runs
                .field(
                        newFieldDefinition()
                                .name("validationRuns")
                                .description("List of runs for this validation stamp")
                                .type(GraphqlUtils.stdList(validationRun.type))
                                .argument {
                                    it.name("count")
                                            .description("Maximum number of validation runs")
                                            .type(Scalars.GraphQLInt)
                                            .defaultValue(50)
                                }
                                .dataFetcher(validationStampValidationRunsFetcher())
                                .build()
                )
                // OK
                .build()

    }

    private fun validationStampValidationRunsFetcher() =
            DataFetcher<List<ValidationRun>> { environment ->
                val validationStamp: ValidationStamp = environment.getSource()
                // Gets all the validation runs
                return@DataFetcher structureService.getValidationRunsForValidationStamp(
                        validationStamp.id,
                        0,
                        environment.getArgument<Int?>("count") ?: 50
                )
            }

    override fun getSignature(entity: ValidationStamp): Optional<Signature> {
        return Optional.ofNullable(entity.signature)
    }

    companion object {

        val VALIDATION_STAMP = "ValidationStamp"
    }

}
