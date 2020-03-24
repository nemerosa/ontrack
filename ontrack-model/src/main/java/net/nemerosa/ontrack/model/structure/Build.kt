package net.nemerosa.ontrack.model.structure

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonView
import net.nemerosa.ontrack.model.buildfilter.BuildDiff
import net.nemerosa.ontrack.model.form.Form
import java.time.LocalDateTime

class Build(
        override val id: ID,
        val name: String,
        override val description: String?,
        override val signature: Signature,
        @JsonView(value = [Build::class, BuildView::class, PromotionRun::class, ValidationRun::class, BuildDiff::class])
        val branch: Branch
) : RunnableEntity {

    @JsonIgnore
    override val runnableEntityType: RunnableEntityType = RunnableEntityType.build

    @JsonIgnore
    override val runMetricName: String = name

    override val runMetricTags: Map<String, String>
        @JsonIgnore
        get() = mapOf(
                "project" to branch.project.name,
                "branch" to branch.name
        )

    override val runTime: LocalDateTime
        @JsonIgnore
        get() = signature.time

    override val project: Project
        get() = branch.project

    override val projectEntityType: ProjectEntityType = ProjectEntityType.BUILD

    override val entityDisplayName: String
        get() = "Build ${branch.project.name}/${branch.name}/$name"

    fun withSignature(signature: Signature) = Build(id, name, description, signature, branch)

    fun withId(id: ID) = Build(id, name, description, signature, branch)

    companion object {

        @JvmStatic
        fun of(branch: Branch, nameDescription: NameDescription, signature: Signature) =
                Build(
                        ID.NONE,
                        nameDescription.name,
                        nameDescription.description,
                        signature,
                        branch
                )

        @JvmStatic
        fun form(): Form = Form.nameAndDescription()

    }

    fun asForm(): Form = form()
            .fill("name", name)
            .fill("description", description)

    fun update(nameDescription: NameDescription): Build = Build(
            id,
            nameDescription.name,
            nameDescription.description,
            signature,
            branch
    )
}
