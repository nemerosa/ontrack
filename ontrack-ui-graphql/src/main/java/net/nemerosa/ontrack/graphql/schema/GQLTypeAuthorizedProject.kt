package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.graphql.support.toNotNull
import net.nemerosa.ontrack.model.security.ProjectRoleAssociation
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component

/**
 * @see net.nemerosa.ontrack.model.security.ProjectRoleAssociation
 */
@Component
class GQLTypeAuthorizedProject(
        private val projectRole: GQLTypeProjectRole,
        private val structureService: StructureService,
) : GQLType {

    override fun getTypeName(): String = AUTHORIZED_PROJECT

    override fun createType(cache: GQLTypeCache): GraphQLObjectType {
        return GraphQLObjectType.newObject()
                .name(AUTHORIZED_PROJECT)
                .field {
                    it.name("role")
                            .description("Role for the project")
                            .type(projectRole.typeRef.toNotNull())
                            .dataFetcher { env ->
                                val pra: ProjectRoleAssociation = env.getSource()
                                pra.projectRole
                            }
                }
                .field {
                    it.name("project")
                            .description("Authorized project")
                            .type(GraphQLNonNull(GraphQLTypeReference(GQLTypeProject.PROJECT)))
                            .dataFetcher { env ->
                                val pra: ProjectRoleAssociation = env.getSource()
                                structureService.getProject(ID.of(pra.projectId))
                            }
                }
                .build()
    }

    companion object {
        const val AUTHORIZED_PROJECT = "AuthorizedProject"
    }
}
