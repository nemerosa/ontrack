package net.nemerosa.ontrack.model.structure

import java.beans.ConstructorProperties
import java.time.LocalDateTime

data class PromotionRunRequest
@ConstructorProperties("promotionLevelId", "promotionLevelName", "dateTime", "description", "properties")
constructor(
        val promotionLevelId: Int?,
        val promotionLevelName: String?,
        val dateTime: LocalDateTime,
        val description: String?,
        val properties: List<PropertyCreationRequest> = emptyList()
)
