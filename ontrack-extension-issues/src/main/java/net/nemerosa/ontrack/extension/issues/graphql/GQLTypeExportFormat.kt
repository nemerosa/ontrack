package net.nemerosa.ontrack.extension.issues.graphql

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.issues.export.ExportFormat
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.stringField
import org.springframework.stereotype.Component

@Component
class GQLTypeExportFormat : GQLType {
    override fun getTypeName(): String = ExportFormat::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Definition of an export format for the issues.")
            .stringField(ExportFormat::id, "ID of the format")
            .stringField(ExportFormat::name, "Display name of the format")
            .stringField(ExportFormat::type, "MIME type of the format")
            .build()
}