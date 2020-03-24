package net.nemerosa.ontrack.model.structure

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.annotation.JsonView
import net.nemerosa.ontrack.model.form.Form

@JsonPropertyOrder(value = ["id", "name", "description", "branch", "image"])
class PromotionLevel(
        override val id: ID,
        val name: String,
        override val description: String?,
        @JsonView(value = [PromotionLevel::class, PromotionView::class, PromotionRunView::class])
        val branch: Branch,
        val isImage: Boolean,
        override val signature: Signature
) : ProjectEntity {

    companion object {
        @JvmStatic
        fun of(branch: Branch, nameDescription: NameDescription): PromotionLevel {
            Entity.isEntityDefined(branch, "Branch must be defined")
            Entity.isEntityDefined(branch.project, "Project must be defined")
            return PromotionLevel(
                    ID.NONE,
                    nameDescription.name,
                    nameDescription.description,
                    branch,
                    isImage = false,
                    signature = Signature.anonymous()
            )
        }

        @JvmStatic
        fun form(): Form = Form.nameAndDescription()
    }

    override val project: Project
        get() = branch.project

    override val projectEntityType: ProjectEntityType = ProjectEntityType.PROMOTION_LEVEL

    override val entityDisplayName: String
        get() = "Promotion level ${branch.project.name}/${branch.name}/$name"

    fun withDescription(description: String?) = PromotionLevel(id, name, description, branch, isImage, signature)

    fun withSignature(signature: Signature) = PromotionLevel(id, name, description, branch, isImage, signature)

    fun withId(id: ID) = PromotionLevel(id, name, description, branch, isImage, signature)

    fun withImage(isImage: Boolean) = PromotionLevel(id, name, description, branch, isImage, signature)

    fun asForm(): Form = form()
            .fill("name", name)
            .fill("description", description)

    fun update(nameDescription: NameDescription): PromotionLevel = PromotionLevel(
            id,
            nameDescription.name,
            nameDescription.description,
            branch,
            isImage,
            signature
    )
}
