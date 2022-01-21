package net.nemerosa.ontrack.model.structure

class SearchResultNode(
        val index: String,
        val id: String,
        val score: Double,
        val source: Map<String, Any?>
)