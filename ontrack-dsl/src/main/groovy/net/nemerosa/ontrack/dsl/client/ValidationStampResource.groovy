package net.nemerosa.ontrack.dsl.client

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.dsl.Ontrack
import net.nemerosa.ontrack.dsl.ValidationStamp
import net.nemerosa.ontrack.dsl.properties.ProjectEntityProperties
import net.nemerosa.ontrack.dsl.properties.ValidationStampProperties
import net.nemerosa.ontrack.json.JsonUtils

class ValidationStampResource extends AbstractProjectResource implements ValidationStamp {

    ValidationStampResource(Ontrack ontrack, JsonNode node) {
        super(ontrack, node)
    }

    @Override
    String getProject() {
        JsonUtils.get(node.path('project'), 'name')
    }

    @Override
    String getBranch() {
        JsonUtils.get(node.path('project').path('branch'), 'name')
    }

    @Override
    ProjectEntityProperties getProperties() {
        new ValidationStampProperties(ontrack, this)
    }
}
