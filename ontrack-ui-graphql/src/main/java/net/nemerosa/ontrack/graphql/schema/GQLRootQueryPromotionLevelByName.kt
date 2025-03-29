package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.support.stringArgument
import net.nemerosa.ontrack.model.settings.PredefinedPromotionLevelService
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@Component
class GQLRootQueryPromotionLevelByName(
    private val gqlTypePromotionLevel: GQLTypePromotionLevel,
    private val structureService: StructureService,
) : GQLRootQuery {

    override fun getFieldDefinition(): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name("promotionLevelByName")
            .description("Promotion level by name")
            .type(gqlTypePromotionLevel.typeRef)
            .argument(stringArgument("project", "Project name", nullable = false))
            .argument(stringArgument("branch", "Branch name", nullable = false))
            .argument(stringArgument("name", "Promotion level name", nullable = false))
            .dataFetcher { env ->
                val project: String = env.getArgument("project")!!
                val branch: String = env.getArgument("branch")!!
                val name: String = env.getArgument("name")!!
                structureService.findPromotionLevelByName(project, branch, name).getOrNull()
            }
            .build()
}