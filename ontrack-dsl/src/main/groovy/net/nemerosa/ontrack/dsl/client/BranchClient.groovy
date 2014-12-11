package net.nemerosa.ontrack.dsl.client

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.dsl.Branch
import net.nemerosa.ontrack.dsl.Build
import net.nemerosa.ontrack.dsl.OntrackConnector

class BranchClient implements Branch {

    private final OntrackConnector connector
    private final JsonNode node

    BranchClient(OntrackConnector connector, JsonNode node) {
        this.connector = connector
        this.node = node
    }

    @Override
    String getProject() {
        // FIXME Method net.nemerosa.ontrack.dsl.Branch.getProject
        return null
    }

    @Override
    String getName() {
        // FIXME Method net.nemerosa.ontrack.dsl.Branch.getName
        return null
    }

    @Override
    String geDescription() {
        // FIXME Method net.nemerosa.ontrack.dsl.Branch.geDescription
        return null
    }

    @Override
    List<Build> filter(String filterType, Map<String, ?> filterConfig) {
        def url = query(
                "${link('_view')}/${filterType}",
                filterConfig
        )
        list(url).collect { BuildClient.of(client, it) }
    }

}
