package net.nemerosa.ontrack.extension.github.ingestion.settings

object GitHubIngestionSettingsHelper {

    fun includes(name: String, includes: String, excludes: String): Boolean {
        val includesRegex = includes.toRegex(RegexOption.IGNORE_CASE)
        val excludesRegex = excludes.takeIf { it.isNotBlank() }?.toRegex(RegexOption.IGNORE_CASE)
        return includesRegex.matches(name) && (excludesRegex == null || !excludesRegex.matches(name))
    }

    fun excludes(name: String, includes: String, excludes: String): Boolean =
        !includes(name, includes, excludes)

}