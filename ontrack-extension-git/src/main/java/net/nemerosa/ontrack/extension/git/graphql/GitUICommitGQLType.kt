package net.nemerosa.ontrack.extension.git.graphql

import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.extension.git.model.GitUICommit
import net.nemerosa.ontrack.extension.git.property.GitCommitPropertyType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeBuild
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter.asObjectType
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.PropertyService
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component

@Component
class GitUICommitGQLType(
    private val propertyService: PropertyService,
    private val structureService: StructureService,
) : GQLType {

    override fun getTypeName(): String = GitUICommit::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        asObjectType(GitUICommit::class, cache) {
            field {
                it.name("build")
                    .description("Build associated with this commit")
                    .type(GraphQLTypeReference(GQLTypeBuild.BUILD))
                    .dataFetcher { env ->
                        val gitUICommit: GitUICommit = env.getSource()
                        propertyService.findByEntityTypeAndSearchArguments(
                            entityType = ProjectEntityType.BUILD,
                            propertyType = GitCommitPropertyType::class,
                            searchArguments = GitCommitPropertyType.getGitCommitSearchArguments(gitUICommit.id)
                        ).firstOrNull()?.let { id ->
                            structureService.getBuild(id)
                        }
                    }
            }
        }

}