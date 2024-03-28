package net.nemerosa.ontrack.extension.scm.mock

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import net.nemerosa.ontrack.common.RunProfile
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Profile(value = [RunProfile.DEV, RunProfile.ACC, RunProfile.UNIT_TEST])
@RestController
@RequestMapping("/extension/scm/mock")
class MockSCMController(
    private val mockSCMExtension: MockSCMExtension,
) {

    /**
     * Registers a file on a branch.
     */
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("/file")
    fun registerFile(@RequestBody registration: FileContentRegistration) {
        mockSCMExtension.repository(registration.name)
            .registerFile(registration.scmBranch, registration.path, registration.content)
    }

    /**
     * Registers an issue in the associated mock issue service
     */
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("/issue")
    fun registerIssue(@RequestBody registration: IssueRegistration) {
        mockSCMExtension.repository(registration.name)
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
        commitId = mockSCMExtension.repository(registration.name)
            .registerCommit(registration.scmBranch, registration.message)
    )

    /**
     * Gets a file content for a branch
     */
    @GetMapping("/file")
    fun getFile(
        @RequestParam repository: String,
        @RequestParam scmBranch: String,
        @RequestParam path: String,
    ): FileContent? =
        mockSCMExtension.repository(repository).getFile(scmBranch, path)?.let {
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
        mockSCMExtension.repository(repository).getBranch(namePattern = scmBranch)
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
        mockSCMExtension.repository(repository).findPR(from, to)
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