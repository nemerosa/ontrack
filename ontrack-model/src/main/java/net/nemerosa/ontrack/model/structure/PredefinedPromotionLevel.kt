package net.nemerosa.ontrack.model.structure

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonPropertyOrder("id", "name", "description", "image")
class PredefinedPromotionLevel(
        override val id: ID,
        val name: String,
        val description: String?,
        @JsonProperty("image")
        val isImage: Boolean
) : Entity {

    fun withDescription(description: String?) = PredefinedPromotionLevel(id, name, description, isImage)

    fun withId(id: ID) = PredefinedPromotionLevel(id, name, description, isImage)

    fun withImage(isImage: Boolean) = PredefinedPromotionLevel(id, name, description, isImage)

    fun update(nameDescription: NameDescription) = PredefinedPromotionLevel(
            id,
            nameDescription.name,
            nameDescription.description,
            isImage
    )

    companion object {

        @JvmStatic
        fun of(nameDescription: NameDescription): PredefinedPromotionLevel {
            return PredefinedPromotionLevel(ID.NONE, nameDescription.name, nameDescription.description, false)
        }

    }
}