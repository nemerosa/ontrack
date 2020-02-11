package net.nemerosa.ontrack.extension.elastic

import net.nemerosa.ontrack.model.search.SearchEqQuery
import net.nemerosa.ontrack.model.search.SearchGtQuery
import net.nemerosa.ontrack.model.search.SearchOrQuery
import net.nemerosa.ontrack.model.search.SearchQuery
import org.elasticsearch.index.query.DisMaxQueryBuilder
import org.elasticsearch.index.query.QueryBuilder
import org.elasticsearch.index.query.RangeQueryBuilder
import org.elasticsearch.index.query.TermQueryBuilder

class ElasticSearchQuery {
    fun of(query: SearchQuery): QueryBuilder =
            when (query) {
                is SearchOrQuery -> DisMaxQueryBuilder().add(of(query.left)).add(of(query.right))
                is SearchGtQuery -> RangeQueryBuilder(query.field).gt(query.operand)
                is SearchEqQuery -> TermQueryBuilder(query.field, query.operand)
            }
}