package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.support.intArgument
import net.nemerosa.ontrack.graphql.support.toNotNull
import net.nemerosa.ontrack.model.structure.ID.Companion.of
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component

@Component
class GQLRootQueryBuild(
    private val structureService: StructureService,
    private val build: GQLTypeBuild,
) : GQLRootQuery {
    override fun getFieldDefinition(): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name("build")
            .description("Getting a build using its ID")
            .type(build.typeRef.toNotNull())
            .argument(
                intArgument("id", "ID of the build to look for", nullable = false)
            )
            .dataFetcher { env ->
                val id: Int = env.getArgument("id")
                structureService.getBuild(of(id))
            }
            .build()

}