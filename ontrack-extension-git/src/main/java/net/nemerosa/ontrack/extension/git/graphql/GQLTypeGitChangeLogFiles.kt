package net.nemerosa.ontrack.extension.git.graphql

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.git.model.GitChangeLogFiles
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.model.annotations.getPropertyDescription
import net.nemerosa.ontrack.model.annotations.getPropertyName
import org.springframework.stereotype.Component

@Component
class GQLTypeGitChangeLogFiles(
    private val gqlTypeGitChangeLogFile: GQLTypeGitChangeLogFile,
) : GQLType {

    override fun getTypeName(): String = GitChangeLogFiles::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("List of files changes")
            .field {
                it.name(getPropertyName(GitChangeLogFiles::list))
                    .description(getPropertyDescription(GitChangeLogFiles::list))
                    .type(listType(gqlTypeGitChangeLogFile.typeRef))
            }
            .build()
}