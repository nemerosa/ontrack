package net.nemerosa.ontrack.extension.influxdb

import net.nemerosa.ontrack.model.security.SecurityService
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpoint

@WebEndpoint(id = "influxdb")
class InfluxDBExtensionActuatorEndPoint(
    private val influxDBConnection: InfluxDBConnection,
    private val securityService: SecurityService
) {

    @WriteOperation
    fun reset() {
        securityService.asAdmin {
            influxDBConnection.reset()
        }
    }

}