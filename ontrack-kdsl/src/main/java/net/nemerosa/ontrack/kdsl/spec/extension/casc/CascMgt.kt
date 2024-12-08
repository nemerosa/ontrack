package net.nemerosa.ontrack.kdsl.spec.extension.casc

import net.nemerosa.ontrack.kdsl.connector.Connected
import net.nemerosa.ontrack.kdsl.connector.Connector
import net.nemerosa.ontrack.kdsl.connector.FileContent

class CascMgt(connector: Connector) : Connected(connector) {
    fun uploadYaml(yaml: String) {
        connector.uploadFile(
            path = "/extension/casc/upload",
            file = FileContent(
                name = "file",
                content = yaml.toByteArray(),
                type = "application/yaml"
            )
        )
    }

}