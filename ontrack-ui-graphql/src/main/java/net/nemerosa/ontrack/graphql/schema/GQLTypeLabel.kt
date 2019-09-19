package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter
import net.nemerosa.ontrack.graphql.support.GraphqlUtils.stdList
import net.nemerosa.ontrack.model.labels.Label
import net.nemerosa.ontrack.model.labels.ProjectLabelManagementService
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component

@Component
class GQLTypeLabel(
        private val fieldContributors: List<GQLFieldContributor>,
        private val projectLabelManagementService: ProjectLabelManagementService,
        private val structureService: StructureService
) : GQLType {
    override fun getTypeName(): String = Label::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
            GraphQLBeanConverter.asObjectTypeBuilder(Label::class.java, cache, emptySet())
                    // List of associated projects
                    .field {
                        it.name("projects")
                                .description("List of associated projects")
                                .type(stdList(GraphQLTypeReference(GQLTypeProject.PROJECT)))
                                .dataFetcher { environment ->
                                    val label: Label = environment.getSource()
                                    val projectIds = projectLabelManagementService.getProjectsForLabel(label)
                                    projectIds.map { id ->
                                        structureService.getProject(id)
                                    }
                                }
                    }
                    // Links
                    .fields(Label::class.java.graphQLFieldContributions(fieldContributors))
                    // OK
                    .build()
}