package net.nemerosa.ontrack.kdsl.spec.search

import net.nemerosa.ontrack.kdsl.connector.graphql.paginate
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.SearchQuery
import net.nemerosa.ontrack.kdsl.connector.graphqlConnector
import net.nemerosa.ontrack.kdsl.connector.support.PaginatedList
import net.nemerosa.ontrack.kdsl.connector.support.emptyPaginatedList
import net.nemerosa.ontrack.kdsl.spec.Ontrack

fun Ontrack.search(token: String): PaginatedList<SearchResult> =
    graphqlConnector.query(
        SearchQuery(token, 40)
    )?.paginate(
        pageInfo = { it.search?.pageInfo?.pageInfoContent },
        pageItems = { it.search?.pageItems }
    )?.map {
        SearchResult(
            title = it.title ?: "",
            description = it.description ?: "",
            accuracy = it.accuracy ?: 0.0,
            type = SearchResultType(
                feature = it.type?.feature?.id ?: "",
                id = it.type?.id ?: "",
                name = it.type?.name ?: "",
                description = it.type?.description ?: "",
            ),
        )
    } ?: emptyPaginatedList()
