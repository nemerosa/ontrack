package net.nemerosa.ontrack.extension.elastic

internal class HitNode(
        val index: String,
        val id: String,
        val score: Double,
        val source: Map<String, Any?>
)