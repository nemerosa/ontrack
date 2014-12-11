package net.nemerosa.ontrack.dsl.client

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.dsl.Branch
import net.nemerosa.ontrack.dsl.Build
import net.nemerosa.ontrack.dsl.OntrackConnector
import net.nemerosa.ontrack.json.JsonUtils

class BranchClient implements Branch {

    private final OntrackConnector connector
    private final JsonNode node

    BranchClient(OntrackConnector connector, JsonNode node) {
        this.connector = connector
        this.node = node
    }

    @Override
    int getId() {
        JsonUtils.getInt(node, 'id')
    }

    @Override
    String getProject() {
        JsonUtils.get(node.path('project'), 'name')
    }

    @Override
    String getName() {
        return JsonUtils.get(node, 'name')
    }

    @Override
    String geDescription() {
        return JsonUtils.get(node, 'description')
    }

    @Override
    List<Build> filter(String filterType, Map<String, ?> filterConfig) {
        def url = query(
                "${link('_view')}/${filterType}",
                filterConfig
        )
        list(url).collect { BuildClient.of(client, it) }
    }

    @Override
    List<Build> getLastPromotedBuilds() {
        filter('net.nemerosa.ontrack.service.PromotionLevelBuildFilterProvider', [:])
    }

    @Override
    Branch promotionLevel(String name, String description) {
        // FIXME Method net.nemerosa.ontrack.dsl.Branch.promotionLevel
        return null
    }

    @Override
    Build build(String name, String description) {
        // FIXME Method net.nemerosa.ontrack.dsl.Branch.build
        return null
    }
}
