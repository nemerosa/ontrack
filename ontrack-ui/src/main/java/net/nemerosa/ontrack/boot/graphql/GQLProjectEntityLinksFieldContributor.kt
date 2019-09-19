package net.nemerosa.ontrack.boot.graphql

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.schema.GQLProjectEntityFieldContributor
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.ui.controller.URIBuilder
import net.nemerosa.ontrack.ui.resource.ResourceDecorator
import org.springframework.stereotype.Component

@Component
class GQLProjectEntityLinksFieldContributor(
        uriBuilder: URIBuilder,
        securityService: SecurityService,
        decorators: List<ResourceDecorator<*>>
) : GQLLinksContributorImpl(
        uriBuilder,
        securityService,
        decorators
), GQLProjectEntityFieldContributor {

    override fun getFields(projectEntityClass: Class<out ProjectEntity>, projectEntityType: ProjectEntityType): List<GraphQLFieldDefinition>? {
        return getFields(projectEntityClass)
    }

}
