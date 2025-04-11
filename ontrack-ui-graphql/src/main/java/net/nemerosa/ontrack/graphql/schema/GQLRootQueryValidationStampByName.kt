package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.support.stringArgument
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@Component
class GQLRootQueryValidationStampByName(
    private val gqlTypeValidationStamp: GQLTypeValidationStamp,
    private val structureService: StructureService,
) : GQLRootQuery {

    override fun getFieldDefinition(): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name("validationStampByName")
            .description("Validation stamp by name")
            .type(gqlTypeValidationStamp.typeRef)
            .argument(stringArgument("project", "Project name", nullable = false))
            .argument(stringArgument("branch", "Branch name", nullable = false))
            .argument(stringArgument("name", "Validation stamp name", nullable = false))
            .dataFetcher { env ->
                val project: String = env.getArgument("project")!!
                val branch: String = env.getArgument("branch")!!
                val name: String = env.getArgument("name")!!
                structureService.findValidationStampByName(project, branch, name).getOrNull()
            }
            .build()
}