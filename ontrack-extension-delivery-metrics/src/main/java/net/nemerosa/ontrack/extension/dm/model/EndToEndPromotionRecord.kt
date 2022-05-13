package net.nemerosa.ontrack.extension.dm.model

data class EndToEndPromotionRecord(
    val depth: Int,
    val ref: EndToEndPromotionNode,
    val target: EndToEndPromotionNode,
)