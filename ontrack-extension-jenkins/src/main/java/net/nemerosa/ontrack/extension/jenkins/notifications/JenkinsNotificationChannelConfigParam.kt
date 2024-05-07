package net.nemerosa.ontrack.extension.jenkins.notifications

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APILabel
import net.nemerosa.ontrack.model.docs.SelfDocumented

/**
 * Parameter to send to a Jenkins job
 */
@SelfDocumented
data class JenkinsNotificationChannelConfigParam(
    @APIDescription("Name of the parameter")
    @APILabel("Name")
    val name: String,
    @APIDescription("Value of the parameter")
    @APILabel("Value")
    val value: String,
)