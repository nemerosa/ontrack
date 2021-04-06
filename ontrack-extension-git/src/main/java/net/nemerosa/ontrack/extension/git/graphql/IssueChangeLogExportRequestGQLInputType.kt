package net.nemerosa.ontrack.extension.git.graphql

import graphql.schema.GraphQLInputObjectType
import graphql.schema.GraphQLInputType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.extension.api.model.IssueChangeLogExportRequest
import net.nemerosa.ontrack.graphql.schema.GQLInputType
import net.nemerosa.ontrack.graphql.schema.stringInputField
import org.springframework.stereotype.Component

/**
 * Input type for [IssueChangeLogExportRequest].
 */
@Component
class IssueChangeLogExportRequestGQLInputType: GQLInputType<IssueChangeLogExportRequest> {

    override fun createInputType(): GraphQLInputType =
        GraphQLInputObjectType.newInputObject()
            .name(IssueChangeLogExportRequest::class.java.simpleName)
            .description("""Request for the export of a change log.
                                
                This request defines how to group issues by type and which format
                to use for the export.
                                
                If no grouping is specified, the issues will be returned as a list, without any grouping.
                                
                The exclude field is used to exclude some issues from the export using their type.
                                
                Note that both the export format and the type of issues are specific notions linked to the issue service
                (JIRA, GitHub...) that produces the issues.""".trimIndent())
            .fields(
                listOf(
                    stringInputField(IssueChangeLogExportRequest::format, nullable = true),
                    stringInputField(IssueChangeLogExportRequest::grouping, """
                        Specification for the grouping, none by default.
                        
                        This specification describes how to group issues using their type. This format is independent from
                        the issue service and this service is responsible for the actual mapping of issue "types" into actual
                        issue fields. For example, for GitHub, an issue type is mapped on a label.
                        The format of the specification is:
                        
                        ----
                        specification := "" | ${'$'}group ( "|" ${'$'}group)*
                        group         := ${'$'}name "=" ${'$'}type ( "," ${'$'}type)*
                        ----
                        
                        For example:
                        
                        ----
                        Bugs=bug|Features=feature,enhancement
                        ----
                        
                        Any issue that would not be mapped in a group will be assigned arbitrarily to the group defined by
                        altGroup property.
                        
                        If the specification is empty, no grouping will be done.
                    """.trimIndent(), nullable = true),
                    stringInputField(IssueChangeLogExportRequest::exclude, nullable = true),
                    stringInputField(IssueChangeLogExportRequest::altGroup, """
                        Title of the group to use when an issue does not belong to any group. It defaults to "Other".
                        If left empty, any issue not belonging to a group would be excluded from the export. This field
                        is not used when no grouping is specified.
                    """.trimIndent(), nullable = true),
                )
            )
            .build()

    override fun convert(argument: Any?):IssueChangeLogExportRequest? {
        return if (argument != null && argument is Map<*,*>) {
            IssueChangeLogExportRequest().apply {
                (argument[IssueChangeLogExportRequest::format.name] as? String)?.let {
                    format = it
                }
                (argument[IssueChangeLogExportRequest::grouping.name] as? String)?.let {
                    grouping = it
                }
                (argument[IssueChangeLogExportRequest::exclude.name] as? String)?.let {
                    exclude = it
                }
                (argument[IssueChangeLogExportRequest::altGroup.name] as? String)?.let {
                    altGroup = it
                }
            }
        } else {
            null
        }
    }

    override fun getTypeRef()= GraphQLTypeReference(IssueChangeLogExportRequest::class.java.simpleName)
}