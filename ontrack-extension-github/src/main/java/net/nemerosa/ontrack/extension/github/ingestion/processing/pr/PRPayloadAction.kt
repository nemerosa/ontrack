package net.nemerosa.ontrack.extension.github.ingestion.processing.pr

enum class PRPayloadAction {

    assigned,
    auto_merge_disabled,
    auto_merge_enabled,

    /**
     * If the action is closed and the merged key is false, the pull request was closed with unmerged commits.
     * If the action is closed and the merged key is true, the pull request was merged.
     */
    closed,
    converted_to_draft,
    edited,
    labeled,
    locked,
    opened,
    ready_for_review,
    reopened,
    review_request_removed,
    review_requested,

    /**
     * Triggered when a pull request's head branch is updated. For example, when the head branch is updated
     * from the base branch, when new commits are pushed to the head branch, or when the base branch is changed.
     */
    synchronize,
    unassigned,
    unlabeled,
    unlocked,

}