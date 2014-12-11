package net.nemerosa.ontrack.dsl.client

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.dsl.Branch
import net.nemerosa.ontrack.dsl.Build
import net.nemerosa.ontrack.dsl.OntrackConnector
import net.nemerosa.ontrack.json.JsonUtils

class BranchResource extends AbstractProjectResource implements Branch {

    BranchResource(OntrackConnector connector, JsonNode node) {
        super(connector, node)
    }

    @Override
    String getProject() {
        JsonUtils.get(node.path('project'), 'name')
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
        post(link('createPromotionLevel'), [
                name       : name,
                description: description
        ])
        this
    }

    @Override
    Build build(String name, String description) {
        // FIXME Method net.nemerosa.ontrack.dsl.Branch.build
        return null
    }
}
