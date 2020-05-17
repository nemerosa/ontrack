package net.nemerosa.ontrack.model.structure;

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Validation stamp defined at global level, allowing some projects to create them automatically.
 *
 * @property dataType Data used for the link to an optional [ValidationDataType] and its configuration
 */
class PredefinedValidationStamp(
        override val id: ID,
        val name: String,
        val description: String?,
        @JsonProperty("image")
        val isImage: Boolean,
        val dataType: ValidationDataTypeConfig<*>?
) : Entity {

    fun withId(id: ID) = PredefinedValidationStamp(id, name, description, isImage, dataType)

    fun withDescription(description: String?) = PredefinedValidationStamp(id, name, description, isImage, dataType)

    fun withDataType(dataType: ValidationDataTypeConfig<*>?) = PredefinedValidationStamp(id, name, description, isImage, dataType)

    fun withImage(isImage: Boolean) = PredefinedValidationStamp(id, name, description, isImage, dataType)

    fun update(nameDescription: NameDescription) =
            PredefinedValidationStamp(
                    id,
                    nameDescription.name,
                    nameDescription.description,
                    isImage,
                    dataType
            )

    companion object {
        @JvmStatic
        fun of(nameDescription: NameDescription): PredefinedValidationStamp =
                PredefinedValidationStamp(
                        ID.NONE,
                        nameDescription.name,
                        nameDescription.description,
                        isImage = false,
                        dataType = null
                )
    }

}
