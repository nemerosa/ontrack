package net.nemerosa.ontrack.extension.svn.support

import net.nemerosa.ontrack.extension.issues.model.Issue

import java.time.LocalDateTime

class MockIssue implements Issue {

    private final int key
    private final MockIssueStatus status
    private final String type

    MockIssue(int key, MockIssueStatus status, String type) {
        this.key = key
        this.status = status
        this.type = type
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
}
