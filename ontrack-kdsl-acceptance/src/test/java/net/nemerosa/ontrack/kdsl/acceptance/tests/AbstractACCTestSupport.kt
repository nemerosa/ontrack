package net.nemerosa.ontrack.kdsl.acceptance.tests

import net.nemerosa.ontrack.kdsl.acceptance.tests.metrics.MetricCollection
import net.nemerosa.ontrack.kdsl.acceptance.tests.metrics.MetricsSupport
import net.nemerosa.ontrack.kdsl.connector.client.Connector
import net.nemerosa.ontrack.kdsl.connector.client.OntractConnectionProperties
import net.nemerosa.ontrack.kdsl.connector.client.OntractMgtConnectionProperties
import net.nemerosa.ontrack.kdsl.spec.Ontrack

abstract class AbstractACCTestSupport {

    /**
     * Connection to the management
     */
    private val ontractMgtConnectionProperties: OntractMgtConnectionProperties by lazy {
        TODO("Get the Ontrack Mgt connection properties from the environment")
    }

    /**
     * Connection to the application
     */
    private val ontractConnectionProperties: OntractConnectionProperties by lazy {
        TODO("Get the Ontrack connection properties from the environment")
    }

    /**
     * Raw connection to the application, without any authentication
     */
    protected fun connector(): Connector = TODO()

    /**
     * Root Ontrack object
     */
    protected val ontrack: Ontrack by lazy {
        TODO("Get the Ontrack root object from the environment")
    }

    /**
     * Getting the metrics
     */
    protected fun getMetrics(): MetricCollection = MetricsSupport.getMetrics(ontractMgtConnectionProperties)

}