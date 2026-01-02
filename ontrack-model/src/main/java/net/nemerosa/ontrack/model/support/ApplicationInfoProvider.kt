package net.nemerosa.ontrack.model.support

/**
 * This interface defines a component that is able to return a list of messages about the state
 * of the application.
 */
interface ApplicationInfoProvider {
    val applicationInfoList: List<ApplicationInfo>
}
