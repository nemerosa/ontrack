package net.nemerosa.ontrack.extension.issues.graphql

import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.extension.issues.model.Issue
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.schema.GQLTypeValidationRun
import net.nemerosa.ontrack.graphql.support.GraphqlUtils.stdList
import net.nemerosa.ontrack.model.structure.ValidationRun
import org.springframework.stereotype.Component

/**
 * Association between an [Issue] and a list of [ValidationRun]s.
 */
@Component
class GQLTypeValidationIssue(
        private val issue: GQLTypeIssue
) : GQLType {

    override fun getTypeName(): String = "ValidationIssue"

    override fun createType(cache: GQLTypeCache) = GraphQLObjectType.newObject()
            .name(typeName)
            .description("Association between an issue and some validation runs.")
            .field {
                it.name("issue")
                        .description("Associated issue")
                        .type(issue.typeRef)
            }
            .field {
                it.name("validationRuns")
                        .description("List of validation runs where this issue was reported")
                        .type(stdList(GraphQLTypeReference(GQLTypeValidationRun.VALIDATION_RUN)))
            }
            .build()

    /**
     * Data to associate with this type.
     */
    class Data(
            val issue: Issue,
            val validationRuns: List<ValidationRun>
    )
}