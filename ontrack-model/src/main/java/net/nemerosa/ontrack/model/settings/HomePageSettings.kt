package net.nemerosa.ontrack.model.settings

import net.nemerosa.ontrack.model.annotations.APIDescription

/**
 * Settings to configure the home page.
 *
 * @property maxBranches Maximum of branches to display per favorite project
 * @property maxProjects Maximum of projects starting from which we need to switch to a search mode
 */
@APIDescription("Settings to configure the home page.")
class HomePageSettings(
    @APIDescription("Maximum of branches to display per favorite project")
    val maxBranches: Int,
    @APIDescription("Maximum of projects starting from which we need to switch to a search mode")
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
