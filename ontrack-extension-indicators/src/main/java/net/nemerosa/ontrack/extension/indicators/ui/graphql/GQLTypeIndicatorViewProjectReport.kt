package net.nemerosa.ontrack.extension.indicators.ui.graphql

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.indicators.portfolio.IndicatorCategoryStats
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.schema.GQLTypeProject
import net.nemerosa.ontrack.graphql.support.field
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.model.structure.Project
import org.springframework.stereotype.Component

@Component
class GQLTypeIndicatorViewProjectReport(
    private val gqlTypeIndicatorCategoryStats: GQLTypeIndicatorCategoryStats,
) : GQLType {

    override fun getTypeName(): String = IndicatorViewProjectReport::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType = GraphQLObjectType.newObject()
        .name(typeName)
        .description("View report line for a project")
        // Linked project
        .field(IndicatorViewProjectReport::project, GQLTypeProject.PROJECT, "Associated project")
        // List of stats per category in the view
        .field {
            it.name(IndicatorViewProjectReport::viewStats.name)
                .description("List of stats per category in the view")
                .type(listType(gqlTypeIndicatorCategoryStats.typeRef))
        }
        // OK
        .build()
}

/**
 * View report line for a project.
 *
 * @property project Associated project
 * @property viewStats List of stats per category in the view
 */
class IndicatorViewProjectReport(
    val project: Project,
    val viewStats: List<IndicatorCategoryStats>,
)
