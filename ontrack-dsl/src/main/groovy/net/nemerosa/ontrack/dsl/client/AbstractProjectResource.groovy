package net.nemerosa.ontrack.dsl.client

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.dsl.OntrackConnector
import net.nemerosa.ontrack.json.JsonUtils

class AbstractProjectResource extends AbstractResource {

    AbstractProjectResource(OntrackConnector connector, JsonNode node) {
        super(connector, node)
    }

    int getId() {
        JsonUtils.getInt(node, 'id')
    }

    String getName() {
        return JsonUtils.get(node, 'name')
    }

    String geDescription() {
        return JsonUtils.get(node, 'description')
    }
}
