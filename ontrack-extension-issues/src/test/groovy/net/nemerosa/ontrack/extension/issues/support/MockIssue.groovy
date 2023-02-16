package net.nemerosa.ontrack.extension.issues.support

import net.nemerosa.ontrack.extension.issues.model.Issue

import java.time.LocalDateTime

/**
 * @deprecated Switch to Kotlin
 */
@Deprecated
class MockIssue implements Issue {

    private final int key
    private final MockIssueStatus status
    private final String type
    private final Collection<MockIssue> links

    MockIssue(int key, MockIssueStatus status, String type, Collection<MockIssue> links) {
        this.key = key
        this.status = status
        this.type = type
        this.links = links
    }

    MockIssue(int key, MockIssueStatus status, String type) {
        this(key, status, type, [])
    }

    void withLinks(Collection<MockIssue> issues) {
        links.clear()
        links.addAll(issues)
    }

    String getType() {
        return type
    }

    @Override
    String getKey() {
        return key as String
    }

    @Override
    MockIssueStatus getStatus() {
        status
    }

    @Override
    String getDisplayKey() {
        "#${key}"
    }

    @Override
    String getSummary() {
        "Issue #${key}"
    }

    @Override
    String getUrl() {
        "uri:issue/${key}"
    }

    @Override
    LocalDateTime getUpdateTime() {
        LocalDateTime.of(2014, 12, 10, 8, 32, key % 60)
    }

    Collection<MockIssue> getLinks() {
        return links
    }
}
