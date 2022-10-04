package net.nemerosa.ontrack.extension.scm.catalog.sync

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APILabel

/**
 * Settings for the SCM catalog synchronization.
 *
 * @property syncEnabled If synchronization of SCM catalog entries as Ontrack projects is enabled
 * @property scm Filter on the SCM type (regex)
 * @property config Filter on the SCM config name (regex)
 * @property repository Filter on the SCM repository name (regex)
 */
class SCMCatalogSyncSettings(
    val syncEnabled: Boolean,
    @APIDescription("Automatically disables the projects which do not have a SCM entry any longer")
    @APILabel("Orphan disabling")
    val orphanDisablingEnabled: Boolean = DEFAULT_SCM_CATALOG_SYNC_SETTINGS_ORPHAN_DISABLED,
    val scm: String? = null,
    val config: String? = null,
    val repository: String? = null,
)

const val DEFAULT_SCM_CATALOG_SYNC_SETTINGS_ENABLED = false
const val DEFAULT_SCM_CATALOG_SYNC_SETTINGS_ORPHAN_DISABLED = false
const val DEFAULT_SCM_CATALOG_SYNC_SETTINGS_SCM: String = ""
const val DEFAULT_SCM_CATALOG_SYNC_SETTINGS_CONFIG: String = ""
const val DEFAULT_SCM_CATALOG_SYNC_SETTINGS_REPOSITORY: String = ""
