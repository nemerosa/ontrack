package net.nemerosa.ontrack.service.elasticsearch

import net.nemerosa.ontrack.model.search.*
import org.elasticsearch.index.query.DisMaxQueryBuilder
import org.elasticsearch.index.query.QueryBuilder
import org.elasticsearch.index.query.RangeQueryBuilder
import org.elasticsearch.index.query.TermQueryBuilder

class ElasticSearchQuery {
    fun of(query: SearchQuery): QueryBuilder =
            when (query) {
                is SearchOrQuery -> DisMaxQueryBuilder().add(of(query.left)).add(of(query.right))
                is SearchGtQuery -> RangeQueryBuilder(query.field).gt(query.operand)
                is SearchLtQuery -> RangeQueryBuilder(query.field).lt(query.operand)
                is SearchEqQuery -> TermQueryBuilder(query.field, query.operand)
            }
}