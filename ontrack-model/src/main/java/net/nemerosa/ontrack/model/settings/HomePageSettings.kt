package net.nemerosa.ontrack.model.settings

/**
 * Settings to configure the home page.
 *
 * @property maxBranches Maximum of branches to display per favorite project
 * @property maxProjects Maximum of projects starting from which we need to switch to a search mode
 */
class HomePageSettings(
    val maxBranches: Int,
    val maxProjects: Int
)

/**
 * Default maximum of branches to display per favorite project
 */
const val DEFAULT_HOME_PAGE_SETTINGS_MAX_BRANCHES = 5

/**
 * Default maximum of projects starting from which we need to switch to a search mode
 */
const val DEFAULT_HOME_PAGE_SETTINGS_MAX_PROJECTS = 20
