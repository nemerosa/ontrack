package net.nemerosa.ontrack.extension.indicators.ui.graphql

import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.extension.indicators.ui.ProjectIndicators
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.schema.GQLTypeProject
import net.nemerosa.ontrack.graphql.support.GraphqlUtils.stdList
import org.springframework.stereotype.Component

@Component
class GQLTypeProjectIndicators(
        private val projectCategoryIndicators: GQLTypeProjectCategoryIndicators
) : GQLType {

    override fun createType(cache: GQLTypeCache): GraphQLObjectType = GraphQLObjectType.newObject()
            .name(typeName)
            .description("List of indicators for a project")
            .field {
                it.name(ProjectIndicators::project.name)
                        .description("Associated project")
                        .type(GraphQLTypeReference(GQLTypeProject.PROJECT))
            }
            .field {
                it.name(ProjectIndicators::categories.name)
                        .description("List of indicator categories")
                        .type(stdList(projectCategoryIndicators.typeRef))
            }
            .build()

    override fun getTypeName(): String = ProjectIndicators::class.java.simpleName
}