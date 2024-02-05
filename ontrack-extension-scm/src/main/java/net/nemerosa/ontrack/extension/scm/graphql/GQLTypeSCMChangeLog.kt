package net.nemerosa.ontrack.extension.scm.graphql

import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.scm.changelog.SCMChangeLog
import net.nemerosa.ontrack.extension.scm.service.SCMDetector
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.field
import net.nemerosa.ontrack.graphql.support.getTypeDescription
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.model.annotations.getPropertyDescription
import org.springframework.stereotype.Component

@Component
class GQLTypeSCMChangeLog(
    private val gqlTypeSCMDecoratedCommit: GQLTypeSCMDecoratedCommit,
    private val gqlTypeSCMChangeLogIssues: GQLTypeSCMChangeLogIssues,
    private val scmDetector: SCMDetector,
) : GQLType {

    override fun getTypeName(): String = SCMChangeLog::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description(getTypeDescription(SCMChangeLog::class))
            .field(SCMChangeLog::from)
            .field(SCMChangeLog::to)
            .field {
                it.name(SCMChangeLog::commits.name)
                    .description(getPropertyDescription(SCMChangeLog::commits))
                    .type(listType(gqlTypeSCMDecoratedCommit.typeRef))
            }
            .field(
                SCMChangeLog::issues,
                gqlTypeSCMChangeLogIssues
            )

            .field {
                it.name("diffLink")
                    .description("URL to get the file diff between the two builds")
                    .type(GraphQLString)
                    .dataFetcher { env ->
                        val changeLog = env.getSource<SCMChangeLog>()
                        val project = changeLog.from.project
                        scmDetector.getSCM(project)?.getDiffLink(changeLog.fromCommit, changeLog.toCommit)
                    }
            }

            .build()
}