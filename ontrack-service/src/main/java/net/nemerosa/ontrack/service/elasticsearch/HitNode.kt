package net.nemerosa.ontrack.service.elasticsearch

internal class HitNode(
        val index: String,
        val id: String,
        val score: Double,
        val source: Map<String, Any?>
)