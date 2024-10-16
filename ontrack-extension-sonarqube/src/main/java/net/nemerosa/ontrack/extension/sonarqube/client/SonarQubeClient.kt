package net.nemerosa.ontrack.extension.sonarqube.client

interface SonarQubeClient {

    /**
     * Gets a list of measure for a given [version] in a project identified by its [key] and the [branch] (Git branch), for a given
     * list of [metrics].
     *
     * The map contains `null` for a metric if the metric was present but could not be converted.
     */
    fun getMeasuresForVersion(
        key: String,
        branch: String,
        version: String,
        metrics: List<String>
    ): Map<String, Double?>?

    /**
     * Server version
     */
    val serverVersion: String

}