package net.nemerosa.ontrack.model.preferences

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import net.nemerosa.ontrack.common.api.APIDescription

/**
 * Representation for the preferences of a user.
 *
 * @property branchViewVsNames Displaying the names of the validation stamps
 * @property branchViewVsGroups Grouping validations per status
 * @property themeMode Light/dark theme selected for the web UI
 * @property selectedChangeLogViewKey Way the change log page is read — see
 * `docs/adr/0008-change-log-views.md`
 * @property changeLogSemanticFormat Renderer the semantic change log is rendered with
 * @property changeLogSemanticEmojis Emojis in the semantic change log's section titles
 * @property changeLogSemanticIssues Issues section inside the rendered semantic change log
 * @property changeLogSemanticCommits Whether the commits cell is shown beside the semantic one
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@APIDescription("Preferences of a user")
data class Preferences(
    @APIDescription("Branch view VS names")
    var branchViewVsNames: Boolean = DEFAULT_BRANCH_VIEW_OPTION,
    @APIDescription("Branch view VS groups")
    var branchViewVsGroups: Boolean = DEFAULT_BRANCH_VIEW_OPTION,
    @APIDescription("Dashboard selected by default")
    var dashboardUuid: String? = null,
    @APIDescription("Selected branch view")
    var selectedBranchViewKey: String? = null,
    @APIDescription("Theme selected for the web UI")
    var themeMode: ThemeMode = DEFAULT_THEME_MODE,
    @APIDescription("Selected change log view")
    var selectedChangeLogViewKey: String? = null,
    @APIDescription("Renderer used by the semantic change log view")
    var changeLogSemanticFormat: String = DEFAULT_CHANGE_LOG_SEMANTIC_FORMAT,
    @APIDescription("Emojis in the section titles of the semantic change log")
    var changeLogSemanticEmojis: Boolean = DEFAULT_CHANGE_LOG_SEMANTIC_EMOJIS,
    @APIDescription("Issues section inside the rendered semantic change log")
    var changeLogSemanticIssues: Boolean = DEFAULT_CHANGE_LOG_SEMANTIC_ISSUES,
    @APIDescription("Commits shown beside the semantic change log")
    var changeLogSemanticCommits: Boolean = DEFAULT_CHANGE_LOG_SEMANTIC_COMMITS,
) {
    companion object {
        const val DEFAULT_BRANCH_VIEW_OPTION = false
        val DEFAULT_THEME_MODE = ThemeMode.SYSTEM

        /**
         * Markdown is the syntax of the places this text gets pasted - release notes, PR
         * descriptions, chat.
         */
        const val DEFAULT_CHANGE_LOG_SEMANTIC_FORMAT = "markdown"

        /**
         * On: the rendering reads as the release notes it is meant to become.
         */
        const val DEFAULT_CHANGE_LOG_SEMANTIC_EMOJIS = true

        /**
         * On: the semantic view has no issues panel of its own, so this is the only place its
         * issues appear.
         */
        const val DEFAULT_CHANGE_LOG_SEMANTIC_ISSUES = true

        /**
         * Off: the commits are the classic view's answer to the same question, and the semantic
         * view is chosen to get away from them.
         */
        const val DEFAULT_CHANGE_LOG_SEMANTIC_COMMITS = false
    }
}
