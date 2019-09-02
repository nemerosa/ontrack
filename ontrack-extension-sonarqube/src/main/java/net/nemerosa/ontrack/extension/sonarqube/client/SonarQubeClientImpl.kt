package net.nemerosa.ontrack.extension.sonarqube.client

import net.nemerosa.ontrack.extension.sonarqube.client.model.*
import net.nemerosa.ontrack.extension.sonarqube.configuration.SonarQubeConfiguration
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.web.client.RestTemplate
import java.net.URLEncoder

class SonarQubeClientImpl(
        configuration: SonarQubeConfiguration
) : SonarQubeClient {

    override val serverVersion: String
        get() = restTemplate.getForObject("/api/server/version", String::class.java)

    override val systemHealth: String
        get() = restTemplate.getForObject("/api/system/health", SystemHealth::class.java).health

    override fun getMeasuresForVersion(key: String, version: String, metrics: List<String>): Map<String, Double?>? {

        val analysis: Analysis? = paginateUntil(
                ProjectAnalysisSearch::class.java,
                uri = { page -> "/api/project_analyses/search?project=${key.encode()}&category=VERSION&p=$page" },
                search = { result ->
                    result.analyses.find { analysis ->
                        analysis.events.any { event -> event.name == version }
                    }
                }
        )

        if (analysis != null) {
            // Timestamp of the version
            val timestamp = analysis.date
            // History measures
            val measures: MeasureSearchHistory = restTemplate.getForObject(
                    "/api/measures/search_history?component=${key.encode()}&metrics=${metrics.joinToString(",").encode()}&from=${timestamp.encode()}&to=${timestamp.encode()}",
                    MeasureSearchHistory::class.java
            )
            // Converts to measures
            return measures.measures.associate { measure ->
                val name = measure.metric
                val value = measure.history.firstOrNull()?.value
                if (value != null) {
                    try {
                        name to value.toDouble()
                    } catch (ex: NumberFormatException) {
                        name to null
                    }
                } else {
                    name to null
                }
            }
        } else {
            return null
        }

    }

    private val restTemplate: RestTemplate = RestTemplateBuilder()
            .rootUri(configuration.url)
            .basicAuthorization(configuration.password, "") // See https://docs.sonarqube.org/latest/extend/web-api/
            .build()

    private fun <T, R : PagedResult> paginateUntil(
            resultType: Class<R>,
            uri: (page: Int) -> String,
            search: (result: R) -> T?
    ): T? {
        var page = 1
        var result: T? = null
        while (result == null) {
            // URI to call
            val url = uri(page++)
            // Getting the page
            val pageResult: R = restTemplate.getForObject(url, resultType)
            // Empty results?
            if (pageResult.isEmpty) {
                return null
            }
            // Gets a result in there
            result = search(pageResult)
        }
        // Nothing found
        return null
    }

    private fun String.encode() = URLEncoder.encode(this, "UTF-8")


}