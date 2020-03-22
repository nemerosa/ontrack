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
import net.nemerosa.ontrack.ui.resource.ResourceDecoratorDelegate
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class GQLLinksContributorImpl(
        private val uriBuilder: URIBuilder,
        private val securityService: SecurityService,
        private val decorators: List<ResourceDecorator<*>>
) : GQLFieldContributor {

    override fun getFields(type: Class<*>): List<GraphQLFieldDefinition> {
        val resourceContext = DefaultResourceContext(uriBuilder, securityService)
        val definitions = mutableListOf<GraphQLFieldDefinition>()
        // Links
        val typeDecorators = decorators
                .filter { decorator -> decorator.appliesFor(type) }
        val linkNames = typeDecorators
                .flatMap { decorator -> decorator.linkNames }
                .distinct()
        if (linkNames.isNotEmpty()) {
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
                                                                        .dataFetcher(linkDataFetcher(linkName))
                                                                        .build()
                                                            }
                                            )
                                            .build()
                            )
                            .dataFetcher { env -> LinksCache(resourceContext, env.getSource(), typeDecorators) }
                            .build()
            )
        }
        // OK
        return definitions
    }

    private fun linkDataFetcher(linkName: String) =
            DataFetcher<String> { environment ->
                val linksCache = environment.getSource<LinksCache>()
                linksCache.getLink(linkName)
            }

    private class LinksCache(
            private val resourceContext: ResourceContext,
            private val source: Any,
            private val typeDecorators: List<ResourceDecorator<*>>
    ) {

        private val cache = ConcurrentHashMap<String, CachedLink>()

        fun getLink(linkName: String): String? =
                cache.getOrPut(linkName) {
                    computeLink(linkName)
                }.link

        private fun computeLink(linkName: String): CachedLink {
            val link = typeDecorators.mapNotNull { decorator ->
                computeLink(decorator, linkName)
            }.firstOrNull()
            return CachedLink(link)
        }

        private fun <T> computeLink(decorator: ResourceDecorator<T>, linkName: String): String? =
                if (linkName in decorator.linkNames) {
                    @Suppress("UNCHECKED_CAST")
                    val t: T = if (source is ResourceDecoratorDelegate) {
                        source.getLinkDelegate()
                    } else {
                        source
                    } as T
                    val link = decorator.linkByName(t, resourceContext, linkName)
                    link?.href?.toString()
                } else {
                    null
                }

    }

    private class CachedLink(val link: String?)

}
