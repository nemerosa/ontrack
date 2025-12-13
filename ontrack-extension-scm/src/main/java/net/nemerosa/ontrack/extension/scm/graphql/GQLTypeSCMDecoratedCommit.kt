package net.nemerosa.ontrack.extension.scm.graphql

import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.extension.scm.changelog.SCMChangeLogEnabled
import net.nemerosa.ontrack.extension.scm.changelog.SCMDecoratedCommit
import net.nemerosa.ontrack.extension.scm.service.SCMDetector
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeBuild
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.field
import net.nemerosa.ontrack.graphql.support.getTypeDescription
import net.nemerosa.ontrack.graphql.support.toNotNull
import net.nemerosa.ontrack.model.support.MessageAnnotationUtils
import org.springframework.stereotype.Component

@Component
class GQLTypeSCMDecoratedCommit(
    private val gqlTypeSCMCommit: GQLTypeSCMCommit,
    private val scmDetector: SCMDetector,
) : GQLType {

    override fun getTypeName(): String = SCMDecoratedCommit::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description(getTypeDescription(SCMDecoratedCommit::class))
            .field(SCMDecoratedCommit::commit, gqlTypeSCMCommit)

            .field {
                it.name("annotatedMessage")
                    .description("Annotated message with links")
                    .type(GraphQLString.toNotNull())
                    .dataFetcher { env ->
                        val (project, commit) = env.getSource<SCMDecoratedCommit>()!!
                        val scm = scmDetector.getSCM(project)
                        if (scm != null && scm is SCMChangeLogEnabled) {
                            val annotator = scm.getConfiguredIssueService()?.messageAnnotator
                            if (annotator != null) {
                                MessageAnnotationUtils.annotate(commit.message, listOf(annotator))
                            } else {
                                commit.message
                            }
                        } else {
                            commit.message
                        }
                    }
            }

            .field {
                it.name("build")
                    .description("Any build linked to this commit")
                    .type(GraphQLTypeReference(GQLTypeBuild.BUILD))
                    .dataFetcher { env ->
                        val (project, commit) = env.getSource<SCMDecoratedCommit>()!!
                        val scm = scmDetector.getSCM(project)
                        if (scm != null && scm is SCMChangeLogEnabled) {
                            val build = scm.findBuildByCommit(project, commit.id)
                            build
                        } else {
                            null
                        }
                    }
            }

            .build()

}
