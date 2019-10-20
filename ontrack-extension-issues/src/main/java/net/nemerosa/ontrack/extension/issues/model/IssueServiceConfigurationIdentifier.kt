package net.nemerosa.ontrack.extension.issues.model

import org.apache.commons.lang3.StringUtils

/**
 * Representation of the ID of an [net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration].
 */
class IssueServiceConfigurationIdentifier(
        val serviceId: String,
        val name: String
) {

    fun format(): String = "$serviceId//$name"

    companion object {
        @JvmStatic
        fun parse(value: String?): IssueServiceConfigurationIdentifier? {
            return if (value.isNullOrBlank()) {
                null
            } else {
                val serviceId = StringUtils.substringBefore(value, "//").trim()
                val name = StringUtils.substringAfter(value, "//").trim()
                if (StringUtils.isNotBlank(serviceId) && StringUtils.isNotBlank(name)) {
                    IssueServiceConfigurationIdentifier(serviceId, name)
                } else {
                    throw IssueServiceConfigurationIdentifierFormatException(value)
                }
            }
        }
    }

}
