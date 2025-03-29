package net.nemerosa.ontrack.model.structure

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonView
import jakarta.validation.constraints.Size
import net.nemerosa.ontrack.model.annotations.APIDescription

/**
 *
 * @property dataType Data used for the link to an optional {@link ValidationDataType} and its configuration
 */
data class ValidationStamp(
        override val id: ID,
        @Size(max = NAME_MAX_LENGTH)
        @APIDescription("Name of the validation stamp")
        val name: String,
        @APIDescription("Description of the validation stamp")
        override val description: String?,
        @JsonView(value = [ValidationStamp::class])
        val branch: Branch,
        val owner: User?,
        @JsonProperty("image")
        val isImage: Boolean,
        override val signature: Signature,
        val dataType: ValidationDataTypeConfig<*>?
) : ProjectEntity {

    companion object {

        /**
         * Maximum length for the name of a validation stamp.
         */
        const val NAME_MAX_LENGTH = 120

        /**
         * All allowed characters
         */
        private const val NAME_ALLOWED_CHARACTERS = "A-Za-z0-9_\\.\\- "

        /**
         * Format for the name of a validation stamp
         */
        const val NAME_REGEX = "[$NAME_ALLOWED_CHARACTERS]+"

        /**
         * Given any value, converts it to a valid validation stamp name.
         */
        fun normalizeValidationStampName(value: String): String =
            value.replace("[^$NAME_ALLOWED_CHARACTERS]".toRegex(), "-").take(NAME_MAX_LENGTH)

        @JvmStatic
        fun of(branch: Branch, nameDescription: NameDescription): ValidationStamp {
            Entity.isEntityDefined(branch, "Branch must be defined")
            Entity.isEntityDefined(branch.project, "Project must be defined")
            return ValidationStamp(
                    ID.NONE,
                    nameDescription.name,
                    nameDescription.description,
                    branch,
                    owner = null,
                    isImage = false,
                    signature = Signature.anonymous(),
                    dataType = null
            )
        }
    }

    override val project: Project
        get() = branch.project

    override val parent: ProjectEntity? get() = branch

    override val projectEntityType: ProjectEntityType = ProjectEntityType.VALIDATION_STAMP

    override val entityDisplayName: String
        get() = "Validation stamp ${branch.project.name}/${branch.name}/$name"

    fun withDescription(description: String?) = ValidationStamp(id, name, description, branch, owner, isImage, signature, dataType)

    fun withSignature(signature: Signature) = ValidationStamp(id, name, description, branch, owner, isImage, signature, dataType)

    fun withDataType(dataType: ValidationDataTypeConfig<*>?) = ValidationStamp(id, name, description, branch, owner, isImage, signature, dataType)

    fun withId(id: ID) = ValidationStamp(id, name, description, branch, owner, isImage, signature, dataType)

    fun withImage(isImage: Boolean) = ValidationStamp(id, name, description, branch, owner, isImage, signature, dataType)

    fun update(nameDescription: NameDescription) = ValidationStamp(
            id,
            nameDescription.name,
            nameDescription.description,
            branch,
            owner,
            isImage,
            signature,
            dataType
    )
}
