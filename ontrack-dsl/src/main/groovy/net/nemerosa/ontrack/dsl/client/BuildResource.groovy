package net.nemerosa.ontrack.dsl.client

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.dsl.Build
import net.nemerosa.ontrack.dsl.OntrackConnector

class BuildResource extends AbstractProjectResource implements Build {

    BuildResource(OntrackConnector connector, JsonNode node) {
        super(connector, node)
    }

    @Override
    Build promote(String promotion) {
        // FIXME Method net.nemerosa.ontrack.dsl.Build.promote
        return null
    }
}
