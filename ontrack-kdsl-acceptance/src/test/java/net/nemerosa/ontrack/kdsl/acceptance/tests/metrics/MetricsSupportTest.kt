package net.nemerosa.ontrack.kdsl.acceptance.tests.metrics

import org.junit.Test
import kotlin.test.assertEquals

class MetricsSupportTest {

    @Test
    fun `Parsing Prometheus data`() {
        val data = """
            # HELP tomcat_sessions_active_max_sessions Maximum number of sessions in Tomcat
            # TYPE tomcat_sessions_active_max_sessions gauge
            tomcat_sessions_active_max_sessions 0.0
            # HELP ontrack_entity_event_total  
            # TYPE ontrack_entity_event_total gauge
            ontrack_entity_event_total 16.0
            # HELP ontrack_connector_count Number of connectors
            # TYPE ontrack_connector_count gauge
            ontrack_connector_count{type="git",} 0.0
            ontrack_connector_count{type="github",} 1.0
            ontrack_connector_count{type="sonarqube",} 2.0
            ontrack_connector_count{type="artifactory",} 0.0
            # HELP rabbitmq_unrouted_published_total  
            # TYPE rabbitmq_unrouted_published_total counter
            rabbitmq_unrouted_published_total{name="rabbit",} 20.0
            # HELP ontrack_job_duration_ms_seconds  
            # TYPE ontrack_job_duration_ms_seconds summary
            ontrack_job_duration_ms_seconds_count{job_category="core",job_id="git",job_type="connector-status",} 29.0
            ontrack_job_duration_ms_seconds_sum{job_category="core",job_id="git",job_type="connector-status",} 0.229
            ontrack_job_duration_ms_seconds_count{job_category="core",job_id="nop",job_type="connector-status",} 30.0
            ontrack_job_duration_ms_seconds_sum{job_category="core",job_id="nop",job_type="connector-status",} 0.002
        """.trimIndent()
        val metrics = MetricsSupport.parseMetrics(data)
        assertEquals(
            MetricCollection(
                listOf(
                    Metric(
                        "tomcat_sessions_active_max_sessions",
                        "gauge",
                        "Maximum number of sessions in Tomcat",
                        listOf(
                            MetricValue(value = 0.0),
                        ),
                    ),
                    Metric(
                        "ontrack_entity_event_total",
                        "gauge",
                        "",
                        listOf(
                            MetricValue(value = 16.0),
                        ),
                    ),
                    Metric(
                        "ontrack_connector_count",
                        "gauge",
                        "Number of connectors",
                        listOf(
                            MetricValue(value = 0.0, "type" to "git"),
                            MetricValue(value = 1.0, "type" to "github"),
                            MetricValue(value = 2.0, "type" to "sonarqube"),
                            MetricValue(value = 0.0, "type" to "artifactory"),
                        ),
                    ),
                    Metric(
                        "rabbitmq_unrouted_published_total",
                        "counter",
                        "",
                        listOf(
                            MetricValue(value = 20.0, "name" to "rabbit"),
                        ),
                    ),
                    Metric(
                        "ontrack_job_duration_ms_seconds",
                        "summary",
                        "",
                        listOf(
                            MetricValue(
                                value = 29.0,
                                name = "ontrack_job_duration_ms_seconds_count",
                                "job_category" to "core",
                                "job_id" to "git",
                                "job_type" to "connector-status"
                            ),
                            MetricValue(
                                value = 0.229,
                                name = "ontrack_job_duration_ms_seconds_sum",
                                "job_category" to "core",
                                "job_id" to "git",
                                "job_type" to "connector-status"
                            ),
                            MetricValue(
                                value = 30.0,
                                name = "ontrack_job_duration_ms_seconds_count",
                                "job_category" to "core",
                                "job_id" to "nop",
                                "job_type" to "connector-status"
                            ),
                            MetricValue(
                                value = 0.002,
                                name = "ontrack_job_duration_ms_seconds_sum",
                                "job_category" to "core",
                                "job_id" to "nop",
                                "job_type" to "connector-status"
                            ),
                        ),
                    ),
                )
            ),
            metrics
        )
    }

}