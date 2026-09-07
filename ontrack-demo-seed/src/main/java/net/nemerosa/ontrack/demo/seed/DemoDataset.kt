package net.nemerosa.ontrack.demo.seed

import java.time.LocalDateTime

/**
 * Everything the demo shows, as plain data.
 *
 * Keeping the dataset declarative means the seed program itself has no content in it, and
 * a feature that wants a demo adds entries here rather than steps to a procedure.
 */
data class DemoDataset(
    val projects: List<ProjectSpec>,
    val environments: List<EnvironmentSpec> = emptyList(),
    val dashboard: DemoDashboard? = null,
)

/**
 * @property scm The SCM the project's change logs are read from. Only the mock SCM, whose
 * commits the seed writes itself: a real repository would trade a self-contained reset for
 * one depending on credentials and network egress.
 */
data class ProjectSpec(
    val name: String,
    val description: String,
    val branches: List<BranchSpec>,
    val scm: ScmSpec? = null,
)

/**
 * A mock SCM repository behind a project.
 *
 * @property repository Name of the repository, unique on the instance. The seed empties it
 * before it registers anything: the mock SCM holds its repositories on the server, where
 * they outlive the projects the reset deletes.
 * @property issues Issues of the repository's issue service. Registered before any commit,
 * because the mock SCM links a commit to an issue as the commit comes in.
 */
data class ScmSpec(
    val repository: String,
    val issues: List<IssueSpec> = emptyList(),
)

/**
 * @property key Issue key, of the `ABC-123` shape the mock issue service recognises in a
 * commit message.
 * @property type What a change log groups its issues by.
 */
data class IssueSpec(
    val key: String,
    val summary: String,
    val type: String? = null,
)

/**
 * @property scmBranch The branch of the project's SCM repository this branch follows.
 */
data class BranchSpec(
    val name: String,
    val description: String,
    val scmBranch: String? = null,
    val promotionLevels: List<PromotionLevelSpec> = emptyList(),
    val validationStamps: List<ValidationStampSpec> = emptyList(),
    val builds: List<BuildSpec> = emptyList(),
)

data class PromotionLevelSpec(
    val name: String,
    val description: String,
    val workflow: WorkflowSpec? = null,
)

/**
 * A workflow run on every promotion run created on the promotion level it is attached to.
 *
 * @property yaml The workflow definition, in the format `ontrack.workflows.saveYamlWorkflow` accepts.
 */
data class WorkflowSpec(val yaml: String)

data class ValidationStampSpec(
    val name: String,
    val description: String,
)

/**
 * @property name Build name — the opaque run identity, as in a real pipeline.
 * @property release Version carried by the build, set as its release property, which is
 * what Yontrack shows as the build display name.
 * @property links Builds this build uses, resolved after every project exists.
 * @property commits Commit messages, oldest first, registered on the branch's SCM branch
 * when the build is created. The last one is the commit the build was built from; the ones
 * before it are the work that went into it, and are what the change log with the previous
 * build shows.
 */
data class BuildSpec(
    val name: String,
    val description: String,
    val creation: BuildCreation,
    val release: String? = null,
    val promotionLevels: List<String> = emptyList(),
    val validations: List<ValidationSpec> = emptyList(),
    val links: List<BuildRef> = emptyList(),
    val commits: List<String> = emptyList(),
)

/**
 * When a build was created.
 */
sealed interface BuildCreation {

    fun resolve(now: LocalDateTime): LocalDateTime

    /**
     * Relative to the run, so that the curated dataset reads as recent work however long
     * ago it was written.
     */
    data class DaysAgo(val days: Long, val hour: Int = 9, val minute: Int = 0) : BuildCreation {
        override fun resolve(now: LocalDateTime): LocalDateTime =
            now.minusDays(days).withHour(hour).withMinute(minute).withSecond(0).withNano(0)
    }

    /**
     * An absolute instant, for a build mirroring something that really happened at a time
     * of its own — a commit.
     */
    data class At(val time: LocalDateTime) : BuildCreation {
        override fun resolve(now: LocalDateTime): LocalDateTime = time
    }
}

data class ValidationSpec(
    val validationStamp: String,
    val status: ValidationStatus,
    val description: String = "",
)

/**
 * Points at a build of another project, by name.
 */
data class BuildRef(
    val project: String,
    val branch: String,
    val build: String,
)

data class EnvironmentSpec(
    val name: String,
    val order: Int,
    val description: String,
    val tags: List<String> = emptyList(),
    val slots: List<SlotSpec> = emptyList(),
)

/**
 * @property deployed The build to run a full deployment for, so the environment shows
 * something rather than an empty slot.
 */
data class SlotSpec(
    val project: String,
    val description: String,
    val deployed: BuildRef? = null,
)

/**
 * Validation statuses the dataset uses. Yontrack knows more — `DEFECTIVE`, `INTERRUPTED`
 * and the rest; adding one here is how the dataset gets to use it.
 */
enum class ValidationStatus {
    PASSED,
    FAILED,
    WARNING,
}
