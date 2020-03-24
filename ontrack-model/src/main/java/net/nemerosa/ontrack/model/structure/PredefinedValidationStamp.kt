package net.nemerosa.ontrack.model.structure;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Wither;

/**
 * Validation stamp defined at global level, allowing some projects to create them automatically.
 *
 * @property dataType Data used for the link to an optional [ValidationDataType] and its configuration
 */
class PredefinedValidationStamp(
        override val id: ID,
        val name: String,
        val description: String?,
        val isImage: Boolean,
        val dataType: ValidationDataTypeConfig<*>?
) : Entity {

    fun withId(id: ID) = PredefinedValidationStamp(id, name, description, isImage, dataType)

    fun withDescription(description: String?) = PredefinedValidationStamp(id, name, description, isImage, dataType)

    fun withDataType(dataType: ValidationDataTypeConfig<*>?) = PredefinedValidationStamp(id, name, description, isImage, dataType)

    fun withImage(isImage: Boolean) = PredefinedValidationStamp(id, name, description, isImage, dataType)

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


    public PredefinedValidationStamp update(NameDescription nameDescription) {
        return new PredefinedValidationStamp(
                id,
                nameDescription.getName(),
                nameDescription.getDescription(),
                image,
                dataType
        );
    }
}
