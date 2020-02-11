package net.nemerosa.ontrack.model.search

sealed class SearchQuery

class SearchEqQuery(val field: String, val operand: Any) : SearchQuery()
class SearchGtQuery(val field: String, val operand: Any) : SearchQuery()
class SearchOrQuery(val left: SearchQuery, val right: SearchQuery) : SearchQuery()
