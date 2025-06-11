package net.nemerosa.ontrack.model.structure

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonView
import java.time.LocalDateTime

data class Build(
    override val id: ID,
    val name: String,
    override val description: String?,
    override val signature: Signature,
    @JsonView(Build::class,PromotionRun::class, ValidationRun::class)
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

    override val parent: ProjectEntity? get() = branch

    override val projectEntityType: ProjectEntityType = ProjectEntityType.BUILD

    override val entityDisplayName: String
        get() = "Build ${branch.project.name}/${branch.name}/$name"

    fun withSignature(signature: Signature) = Build(id, name, description, signature, branch)

    fun withName(name: String) = Build(id, name, description, signature, branch)
    fun withDescription(description: String) = Build(id, name, description, signature, branch)

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

    }

    fun update(nameDescription: NameDescription): Build = Build(
        id,
        nameDescription.name,
        nameDescription.description,
        signature,
        branch
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Build) return false

        if (id != other.id) return false
        if (name != other.name) return false
        if (description != other.description) return false
        if (signature != other.signature) return false
        if (branch != other.branch) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + signature.hashCode()
        result = 31 * result + branch.hashCode()
        return result
    }


}
