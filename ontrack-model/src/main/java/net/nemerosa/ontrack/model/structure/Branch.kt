package net.nemerosa.ontrack.model.structure

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonView
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.YesNo

/**
 * Representation of a branch inside a [Project]. They are usually associated
 * to a branch of the SCM associated with the parent project.
 */
class Branch(
        override val id: ID,
        val name: String,
        override val description: String?,
        @JsonProperty("disabled")
        val isDisabled: Boolean,
        val type: BranchType,
        @JsonView(value = [
            PromotionView::class, Branch::class, Build::class, PromotionLevel::class, ValidationStamp::class,
            PromotionRun::class, ValidationRun::class, PromotionRunView::class
        ])
        @JsonProperty("project")
        override val project: Project,
        override val signature: Signature
) : ProjectEntity {

    fun withId(id: ID) = Branch(id, name, description, isDisabled, type, project, signature)

    fun withDescription(description: String?) = Branch(id, name, description, isDisabled, type, project, signature)

    fun withDisabled(isDisabled: Boolean) = Branch(id, name, description, isDisabled, type, project, signature)

    fun withType(type: BranchType) = Branch(id, name, description, isDisabled, type, project, signature)

    fun withSignature(signature: Signature) = Branch(id, name, description, isDisabled, type, project, signature)

    companion object {

        @JvmStatic
        fun of(project: Project, nameDescription: NameDescription) =
                of(project, nameDescription.asState())

        @JvmStatic
        fun of(project: Project, nameDescription: NameDescriptionState) =
                Branch(
                        ID.NONE,
                        nameDescription.name,
                        nameDescription.description,
                        nameDescription.isDisabled,
                        BranchType.CLASSIC,
                        project,
                        Signature.anonymous()
                )
    }

    override val projectEntityType: ProjectEntityType = ProjectEntityType.BRANCH

    override val entityDisplayName: String
        get() = "Branch ${project.name}/$name"

    fun asForm(): Form = Form.create()
            .with(
                    Form.defaultNameField().length(120)
            )
            .description()
            .with(
                    YesNo.of("disabled").label("Disabled").help("Check if the branch must be disabled.")
            )

    fun toForm(): Form = asForm()
            .fill("name", name)
            .fill("description", description)
            .fill("disabled", isDisabled)

    fun update(form: NameDescriptionState): Branch =
            of(project, form)
                    .withId(id).withDisabled(form.isDisabled)

}
