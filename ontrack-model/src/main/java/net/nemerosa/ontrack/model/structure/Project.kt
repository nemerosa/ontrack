package net.nemerosa.ontrack.model.structure

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * A project in Ontrack. Usually associated with a SCM repository, this is the main entity in the Ontrack
 * model.
 */
data class Project(
        override val id: ID,
        val name: String,
        override val description: String?,
        @JsonProperty("disabled")
        val isDisabled: Boolean,
        override val signature: Signature
) : ProjectEntity {

    fun withDisabled(isDisabled: Boolean) = Project(id, name, description, isDisabled, signature)

    fun withSignature(signature: Signature) = Project(id, name, description, isDisabled, signature)

    fun withDescription(description: String) = Project(id, name, description, isDisabled, signature)

    fun withId(id: ID) = Project(id, name, description, isDisabled, signature)

    companion object {

        /**
         * Maximum length for the name of a project
         */
        const val PROJECT_NAME_MAX_LENGTH = 80

        @JvmStatic
        fun of(nameDescription: NameDescriptionState) =
                Project(ID.NONE, nameDescription.name, nameDescription.description, nameDescription.isDisabled, Signature.anonymous())

        @JvmStatic
        fun of(nameDescription: NameDescription) =
                Project(ID.NONE, nameDescription.name, nameDescription.description, false, Signature.anonymous())

    }

    override val project: Project get() = this

    override val entityDisplayName: String get() = "Project $name"

    override val parent: ProjectEntity? = null

    override val projectEntityType: ProjectEntityType = ProjectEntityType.PROJECT


    fun update(form: NameDescriptionState): Project =
            of(form).withId(id).withDisabled(form.isDisabled)

}
