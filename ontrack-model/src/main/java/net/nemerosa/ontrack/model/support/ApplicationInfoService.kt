package net.nemerosa.ontrack.model.support

/**
 * This service returns the list of application info messages, as returned by all the
 * registered [net.nemerosa.ontrack.model.support.ApplicationInfoProvider].
 */
interface ApplicationInfoService {
    val applicationInfoList: List<ApplicationInfo>
}
