package net.nemerosa.ontrack.extension.jira.mock

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import net.nemerosa.ontrack.extension.jira.JIRAConfigurationProperties
import net.nemerosa.ontrack.extension.jira.model.JIRAIssue
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.web.bind.annotation.*

@RestController
@ConditionalOnProperty(
    prefix = JIRAConfigurationProperties.JIRA_MOCK_PREFIX,
    name = [JIRAConfigurationProperties.JIRA_MOCK_ENABLED],
    havingValue = "true",
    matchIfMissing = false,
)
@RequestMapping("/extension/jira/mock")
class MockJIRAController(
    private val instance: MockJIRAInstance,
) {

    /**
     * Creating a mock issue
     */
    @PostMapping("/issue")
    fun createIssue(@RequestBody input: MockIssueInput): JIRAIssue =
        instance.registerIssue(
            key = input.key,
            summary = input.message,
            type = input.type,
            linkedKey = input.linkedKey,
        )

    /**
     * Getting an issue by key
     */
    @GetMapping("/issue/{key}")
    fun getIssue(@PathVariable key: String): JIRAIssue? =
        instance.getIssue(key)

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class MockIssueInput(
        val key: String,
        val message: String,
        val linkedKey: String?,
        val type: String?,
    )

}