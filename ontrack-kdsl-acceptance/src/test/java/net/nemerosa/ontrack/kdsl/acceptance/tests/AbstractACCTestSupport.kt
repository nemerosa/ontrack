package net.nemerosa.ontrack.kdsl.acceptance.tests

import net.nemerosa.ontrack.kdsl.acceptance.tests.metrics.MetricCollection
import net.nemerosa.ontrack.kdsl.acceptance.tests.metrics.MetricsSupport
import net.nemerosa.ontrack.kdsl.connector.Connector
import net.nemerosa.ontrack.kdsl.connector.client.OntractConnectionProperties
import net.nemerosa.ontrack.kdsl.connector.client.OntractMgtConnectionProperties
import net.nemerosa.ontrack.kdsl.connector.support.DefaultConnector
import net.nemerosa.ontrack.kdsl.spec.Ontrack

abstract class AbstractACCTestSupport {

    /**
     * Connection to the management
     */
    private val ontractMgtConnectionProperties: OntractMgtConnectionProperties by lazy {
        OntractMgtConnectionProperties(
            url = ACCProperties.Connection.Mgt.url,
            token = null,
        )
    }

    /**
     * Connection to the application
     */
    protected val ontractConnectionProperties: OntractConnectionProperties by lazy {
        OntractConnectionProperties(
            url = ACCProperties.Connection.url,
            token = ACCProperties.Connection.token,
            internalUrl = ACCProperties.Connection.Internal.url,
        )
    }

    /**
     * Connection to the management
     */
    private fun mgtConnector(): Connector = DefaultConnector(
        ontractMgtConnectionProperties.url,
        emptyMap(),
    )

    /**
     * Authenticated connector to the application
     */
    private fun connector(): Connector = DefaultConnector(
        url = ontractConnectionProperties.url,
        defaultHeaders = mapOf(
            "X-Ontrack-Token" to ontractConnectionProperties.token,
        )
    )

    /**
     * Raw connection to the application, without any authentication
     */
    protected fun rawConnector(): Connector = DefaultConnector(
        url = ontractConnectionProperties.url,
    )

    /**
     * Root Ontrack object
     */
    private val rootOntrack: Ontrack by lazy {
        Ontrack(connector = connector())
    }

    private var alternateOntrack: Ontrack? = null

    protected val ontrack: Ontrack get() = alternateOntrack ?: rootOntrack

    protected fun <T> withConnector(connector: Connector, code: () -> T): T {
        val oldAlternateOntrack = alternateOntrack
        return try {
            alternateOntrack = Ontrack(connector = connector)
            code()
        } finally {
            alternateOntrack = oldAlternateOntrack
        }
    }

    /**
     * Getting the metrics
     */
    protected fun getMetrics(): MetricCollection {
        // Gets a connector to the management
        val connector = mgtConnector()
        // Gets the prometheus metrics
        val response = connector.get("/prometheus")
        // Parsing of the response
        return MetricsSupport.parseMetrics(response.body.asText())
    }

}