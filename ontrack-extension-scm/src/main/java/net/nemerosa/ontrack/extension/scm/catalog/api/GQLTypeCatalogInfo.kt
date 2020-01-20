package net.nemerosa.ontrack.extension.scm.catalog.api

import graphql.Scalars.GraphQLString
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.extension.scm.catalog.CatalogInfo
import net.nemerosa.ontrack.extension.scm.catalog.CatalogInfoCollector
import net.nemerosa.ontrack.extension.scm.catalog.CatalogLinkService
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.schema.GQLTypeProject
import net.nemerosa.ontrack.graphql.support.GraphqlUtils.stdList
import net.nemerosa.ontrack.model.structure.Project
import org.springframework.stereotype.Component

@Component
class GQLTypeCatalogInfo(
        private val catalogLinkService: CatalogLinkService,
        private val catalogInfoItem: GQLTypeCatalogInfoItem,
        private val catalogInfoCollector: CatalogInfoCollector
) : GQLType {

    class Data(
            val project: Project
    )

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
            GraphQLObjectType.newObject()
                    .name(typeName)
                    .description("Link between a project and a SCM catalog entry")
                    .field {
                        it.name("project")
                                .description("Linked project")
                                .type(GraphQLTypeReference(GQLTypeProject.PROJECT))
                    }
                    .field {
                        it.name("scmCatalogEntry")
                                .description("Linked catalog entry")
                                .type(GraphQLTypeReference(GQLTypeSCMCatalogEntry.SCM_CATALOG_ENTRY))
                                .dataFetcher { env ->
                                    val data: Data = env.getSource()
                                    val project = data.project
                                    catalogLinkService.getSCMCatalogEntry(project)
                                }
                    }
                    .field {
                        it.name("infos")
                                .description("List of collected information")
                                .type(stdList(catalogInfoItem.typeRef))
                                .argument { a ->
                                    a.name("type")
                                            .description("FQCN of the information type")
                                            .type(GraphQLString)
                                }
                                .dataFetcher { env -> loadInfos(env) }
                    }
                    .build()

    private fun loadInfos(env: DataFetchingEnvironment): List<GQLTypeCatalogInfoItem.Data> {
        val type: String? = env.getArgument("type")
        val project = env.getSource<Data>().project
        return catalogInfoCollector.getCatalogInfos(project)
                .filter {
                    type.isNullOrBlank() || it.collector.id == type
                }.map {
                    toData(it)
                }
    }

    private fun <T> toData(it: CatalogInfo<T>): GQLTypeCatalogInfoItem.Data {
        return GQLTypeCatalogInfoItem.Data(
                id = it.collector.id,
                name = it.collector.name,
                data = it.data?.run { it.collector.asClientJson(this) },
                error = it.error,
                timestamp = it.timestamp,
                feature = it.collector.feature.featureDescription
        )
    }

    override fun getTypeName(): String = "CatalogInfo"

}