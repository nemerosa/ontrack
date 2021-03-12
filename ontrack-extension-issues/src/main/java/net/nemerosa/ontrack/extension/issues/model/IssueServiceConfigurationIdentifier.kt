package net.nemerosa.ontrack.extension.issues.model

/**
 * Representation of the ID of an [net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration].
 */
data class IssueServiceConfigurationIdentifier(
    val serviceId: String,
    val name: String
) {

    fun format(): String = "$serviceId$DELIMITER$name"

    companion object {

        private const val DELIMITER = "//"

        @JvmStatic
        fun parse(value: String?): IssueServiceConfigurationIdentifier? {
            return if (value.isNullOrBlank()) {
                null
            } else {
                val serviceId = value.substringBefore(DELIMITER, missingDelimiterValue = "").trim()
                val name = value.substringAfter(DELIMITER, missingDelimiterValue = "").trim()
                if (serviceId.isNotBlank() && name.isNotBlank()) {
                    IssueServiceConfigurationIdentifier(serviceId, name)
                } else {
                    null
                }
            }
        }
    }

}
