package net.nemerosa.ontrack.extension.environments

data class EnvironmentsLicenseData(
    val maxEnvironments: Int,
) {
    companion object {
        val MAX_ENVIRONMENTS = EnvironmentsLicenseData::maxEnvironments.name
    }
}
