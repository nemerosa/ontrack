package net.nemerosa.ontrack.boot.graphql

import graphql.Scalars.GraphQLString
import graphql.schema.DataFetcher
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLFieldDefinition.newFieldDefinition
import graphql.schema.GraphQLObjectType.newObject
import net.nemerosa.ontrack.graphql.schema.GQLFieldContributor
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.ui.controller.URIBuilder
import net.nemerosa.ontrack.ui.resource.DefaultResourceContext
import net.nemerosa.ontrack.ui.resource.ResourceContext
import net.nemerosa.ontrack.ui.resource.ResourceDecorator
import org.springframework.stereotype.Component

@Component
class GQLLinksContributorImpl(
        private val uriBuilder: URIBuilder,
        private val securityService: SecurityService,
        private val decorators: List<ResourceDecorator<*>>
) : GQLFieldContributor {

    override fun getFields(type: Class<*>): List<GraphQLFieldDefinition> {
        val definitions = mutableListOf<GraphQLFieldDefinition>()
        // Links
        val typeDecorators = decorators
                .filter { decorator -> decorator.appliesFor(type) }
        val linkNames = typeDecorators
                .flatMap { decorator -> decorator.linkNames }
                .distinct()
        if (!linkNames.isEmpty()) {
            definitions.add(
                    newFieldDefinition()
                            .name("links")
                            .description("Links")
                            .type(
                                    newObject()
                                            .name(type.simpleName + "Links")
                                            .description(type.simpleName + " links")
                                            .fields(
                                                    linkNames
                                                            .map { linkName ->
                                                                newFieldDefinition()
                                                                        .name(linkName)
                                                                        .type(GraphQLString)
                                                                        .build()
                                                            }
                                            )
                                            .build()
                            )
                            .dataFetcher(typeLinksFetcher(typeDecorators))
                            .build()
            )
        }
        // OK
        return definitions
    }

    private fun typeLinksFetcher(typeDecorators: List<ResourceDecorator<*>>): DataFetcher<*> {
        return DataFetcher<Any> { environment ->
            val source = environment.getSource<Any>()
            for (decorator in typeDecorators) {
                return@DataFetcher getLinks<Any>(decorator, source)
            }
            emptyMap<Any, Any>()
        }
    }

    private fun <T> getLinks(decorator: ResourceDecorator<*>, source: Any): Map<String, String> {
        @Suppress("UNCHECKED_CAST")
        val resourceDecorator = decorator as ResourceDecorator<T>
        @Suppress("UNCHECKED_CAST")
        val t = source as T
        return resourceDecorator.links(
                t,
                createResourceContext()
        ).associate { link ->
            link.name to link.href.toString()
        }
    }

    private fun createResourceContext(): ResourceContext {
        return DefaultResourceContext(
                uriBuilder,
                securityService
        )
    }

}
