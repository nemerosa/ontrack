package net.nemerosa.ontrack.dsl.client

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.dsl.Ontrack
import net.nemerosa.ontrack.json.JsonUtils

class AbstractProjectResource extends AbstractResource {

    AbstractProjectResource(Ontrack ontrack, JsonNode node) {
        super(ontrack, node)
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
