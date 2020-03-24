package net.nemerosa.ontrack.model.structure

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonView
import java.time.LocalDateTime

/**
 * @property runOrder The run order is the order of run for the build. It starts with 1 for the first run.
 * @property validationRunStatuses Must always contain at least one validation run status at creation time.
 * @property data Data used for the link to an optional [ValidationDataType] and its data
 */
class ValidationRun(
        override val id: ID,
        @JsonView(value = [ValidationRun::class, ValidationStampRunView::class])
        val build: Build,
        @JsonView(value = [ValidationRun::class, Build::class])
        val validationStamp: ValidationStamp,
        val runOrder: Int,
        val data: ValidationRunData<*>?,
        @JsonView(value = [ValidationRun::class, BranchBuildView::class, Build::class, ValidationStampRunView::class])
        val validationRunStatuses: List<ValidationRunStatus>
) : RunnableEntity {

    @JsonIgnore
    override val runnableEntityType: RunnableEntityType = RunnableEntityType.validation_run

    override val runMetricName: String
        @JsonIgnore
        get() = build.name

    override val runMetricTags: Map<String, String>
        @JsonIgnore
        get() = mapOf(
                "project" to validationStamp.branch.project.name,
                "branch" to validationStamp.branch.name,
                "validationStamp" to validationStamp.name,
                "status" to lastStatusId
        )

    override val runTime: LocalDateTime
        get() = signature.time

    /**
     * The validation run, as such, as no description, because it's managed at validation run status level.
     */
    override val description: String? = null

    /**
     * Gets the name of the last status
     */
    val lastStatusId: String =
            if (validationRunStatuses.isEmpty()) {
                ""
            } else {
                validationRunStatuses.first().statusID.id
            }

    fun withData(data: ValidationRunData<*>?) =
            ValidationRun(id, build, validationStamp, runOrder, data, validationRunStatuses)

    fun add(status: ValidationRunStatus): ValidationRun = ValidationRun(
            id,
            build,
            validationStamp,
            runOrder,
            data,
            listOf(status) + validationRunStatuses
    )

    override val project: Project
        get() = build.project

    override val projectEntityType: ProjectEntityType = ProjectEntityType.VALIDATION_RUN

    override val entityDisplayName: String
        get() = "Validation run ${validationStamp.name}#${runOrder} for ${build.branch.project.name}/${build.branch.name}/${build.name}"

    companion object {

        @JvmStatic
        fun of(build: Build, validationStamp: ValidationStamp, runOrder: Int, statuses: List<ValidationRunStatus>) =
                ValidationRun(
                        ID.NONE,
                        build,
                        validationStamp,
                        runOrder,
                        data = null,
                        validationRunStatuses = statuses
                )

        @JvmStatic
        fun of(
                build: Build,
                validationStamp: ValidationStamp,
                runOrder: Int,
                signature: Signature,
                validationRunStatusID: ValidationRunStatusID,
                description: String?
        ) = of(
                build,
                validationStamp,
                runOrder,
                listOf(
                        ValidationRunStatus(ID.NONE, signature, validationRunStatusID, description)
                )
        )
    }

    fun withId(id: ID) = ValidationRun(id, build, validationStamp, runOrder, data, validationRunStatuses)

    @JsonProperty("passed")
    val isPassed: Boolean = lastStatus.isPassed

    /**
     * The last status ("last" from a business point of view) is actually the first one in the list of statuses because
     * statuses are sorted from the most recent one to the least recent one.
     */
    val lastStatus: ValidationRunStatus
        get() = validationRunStatuses.first()

    override val signature: Signature
        get() = lastStatus.signature

}
