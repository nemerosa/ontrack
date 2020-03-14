package net.nemerosa.ontrack.model.search.dsl

import net.nemerosa.ontrack.model.search.*

/**
 * DSL marker for the search DSL
 */
@DslMarker
annotation class SearchQueryDsl

@SearchQueryDsl
fun query(dsl: SearchQueryDslBuilder.() -> SearchQuery): SearchQuery = SearchQueryDslBuilder().dsl()

@SearchQueryDsl
class SearchQueryDslBuilder {

    infix fun String.eq(any: Any): SearchQuery = SearchEqQuery(this, any)

    infix fun String.gt(any: Any): SearchQuery = SearchGtQuery(this, any)
    infix fun String.lt(any: Any): SearchQuery = SearchLtQuery(this, any)

    infix fun SearchQuery.or(other: SearchQuery): SearchQuery = SearchOrQuery(this, other)

}