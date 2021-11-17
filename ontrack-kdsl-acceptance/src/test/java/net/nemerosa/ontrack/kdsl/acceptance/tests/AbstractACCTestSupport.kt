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
    private val ontractConnectionProperties: OntractConnectionProperties by lazy {
        TODO("Get the Ontrack connection properties from the environment")
    }

    /**
     * Connection to the management
     */
    protected fun mgtConnector(): Connector = DefaultConnector(
        ontractMgtConnectionProperties.url,
        emptyMap(),
    )

    /**
     * Raw connection to the application, without any authentication
     */
    protected fun rawConnector(): Connector = TODO()

    /**
     * Root Ontrack object
     */
    protected val ontrack: Ontrack by lazy {
        TODO("Get the Ontrack root object from the environment")
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