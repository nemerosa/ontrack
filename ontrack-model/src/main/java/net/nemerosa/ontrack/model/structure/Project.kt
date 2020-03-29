package net.nemerosa.ontrack.model.structure

import com.fasterxml.jackson.annotation.JsonProperty
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.YesNo

/**
 * A project in Ontrack. Usually associated with a SCM repository, this is the main entity in the Ontrack
 * model.
 */
open class Project(
        override val id: ID,
        val name: String,
        override val description: String?,
        @JsonProperty("disabled")
        val isDisabled: Boolean,
        override val signature: Signature
) : ProjectEntity {

    fun withDisabled(isDisabled: Boolean) = Project(id, name, description, isDisabled, signature)

    fun withSignature(signature: Signature) = Project(id, name, description, isDisabled, signature)

    fun withId(id: ID) = Project(id, name, description, isDisabled, signature)

    companion object {

        @JvmStatic
        fun of(nameDescription: NameDescriptionState) =
                Project(ID.NONE, nameDescription.name, nameDescription.description, nameDescription.isDisabled, Signature.anonymous())

        @JvmStatic
        fun of(nameDescription: NameDescription) =
                Project(ID.NONE, nameDescription.name, nameDescription.description, false, Signature.anonymous())

        @JvmStatic
        fun form(): Form =
                Form.nameAndDescription()
                        .with(
                                YesNo.of("disabled").label("Disabled").help("Check if the project must be disabled.")
                        )
    }

    override val project: Project = this

    override val entityDisplayName: String get() = "Project $name"

    override val projectEntityType: ProjectEntityType = ProjectEntityType.PROJECT


    fun update(form: NameDescriptionState): Project =
            of(form).withId(id).withDisabled(form.isDisabled)

    fun asForm(): Form =
            form().name(name).description(description).fill("disabled", isDisabled)

}
