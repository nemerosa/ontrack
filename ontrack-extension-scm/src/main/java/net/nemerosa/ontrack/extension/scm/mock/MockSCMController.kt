package net.nemerosa.ontrack.extension.scm.mock

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.SecurityService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@ConditionalOnProperty(
    prefix = "ontrack.config.extension.scm.mock",
    name = ["enabled"],
    havingValue = "true",
    matchIfMissing = false,
)
@RestController
@RequestMapping("/extension/scm/mock")
class MockSCMController(
    private val mockSCMExtension: MockSCMExtension,
    private val securityService: SecurityService,
) {

    /**
     * Registers a file on a branch.
     */
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("/file")
    fun registerFile(@RequestBody registration: FileContentRegistration) {
        mockSCMExtension.repositoryOrCreate(registration.name)
            .registerFile(registration.scmBranch, registration.path, registration.content)
    }

    /**
     * Registers an issue in the associated mock issue service
     */
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("/issue")
    fun registerIssue(@RequestBody registration: IssueRegistration) {
        mockSCMExtension.repositoryOrCreate(registration.name)
            .registerIssue(
                registration.key,
                registration.message,
                *registration.types(),
            )
    }

    /**
     * Registers a commit in the repository for a given branch
     */
    @PostMapping("/commit")
    fun registerCommit(@RequestBody registration: CommitRegistration) = CommitResponse(
        commitId = mockSCMExtension.repositoryOrCreate(registration.name)
            .registerCommit(registration.scmBranch, registration.message)
    )

    /**
     * Deletes a repository and everything it holds.
     *
     * The mock SCM keeps its repositories on the bean, where they outlive the entities
     * pointing at them: a seed re-registering the same commits would otherwise number them
     * on top of the ones already there, and give every one of them a different id.
     */
    @ResponseStatus(HttpStatus.ACCEPTED)
    @DeleteMapping("/repository")
    fun deleteRepository(@RequestParam repository: String) {
        // The endpoints beside this one only add mock data; this one destroys it, and the
        // mock SCM now runs on long-lived instances - the demo included - where
        // `/extension/**` asks for no more than an authenticated user.
        securityService.checkGlobalFunction(GlobalSettings::class.java)
        mockSCMExtension.deleteRepository(repository)
    }

    /**
     * Gets a file content for a branch
     */
    @GetMapping("/file")
    fun getFile(
        @RequestParam repository: String,
        @RequestParam scmBranch: String,
        @RequestParam path: String,
    ): FileContent? =
        mockSCMExtension.repositoryOrCreate(repository).getFile(scmBranch, path)?.let {
            FileContent(it)
        }

    /**
     * Gets a branch by its name
     */
    @GetMapping("/branch")
    fun getBranch(
        @RequestParam repository: String,
        @RequestParam scmBranch: String,
    ): MockBranch =
        mockSCMExtension.repositoryOrCreate(repository).getBranch(namePattern = scmBranch)
            ?: throw MockSCMBranchNotFoundException(scmBranch)

    /**
     * Gets a PR using a filter
     */
    @GetMapping("/pr")
    fun findPR(
        @RequestParam repository: String,
        @RequestParam from: String?,
        @RequestParam to: String?,
    ): MockSCMExtension.MockPullRequest =
        mockSCMExtension.repositoryOrCreate(repository).findPR(from, to)
            ?: throw MockSCMPullRequestNotFoundException()

    data class FileContentRegistration(
        val name: String,
        val scmBranch: String,
        val path: String,
        val content: String,
    )

    data class FileContent(
        val text: String,
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class IssueRegistration(
        val name: String,
        val key: String,
        val type: String? = null,
        val message: String,
    ) {
        fun types() = if (type.isNullOrBlank()) {
            emptyArray()
        } else {
            arrayOf(type)
        }
    }

    data class CommitRegistration(
        val name: String,
        val scmBranch: String,
        val message: String,
    )

    data class CommitResponse(
        val commitId: String,
    )

}