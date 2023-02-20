package net.nemerosa.ontrack.extension.git.graphql

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.git.model.GitChangeLogFile
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.enumField
import net.nemerosa.ontrack.graphql.support.stringField
import org.springframework.stereotype.Component

@Component
class GQLTypeGitChangeLogFile : GQLType {

    override fun getTypeName(): String = GitChangeLogFile::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Change on one file")
            .enumField(GitChangeLogFile::changeType)
            .stringField(GitChangeLogFile::oldPath)
            .stringField(GitChangeLogFile::newPath)
            .stringField(GitChangeLogFile::url)
            .build()
}