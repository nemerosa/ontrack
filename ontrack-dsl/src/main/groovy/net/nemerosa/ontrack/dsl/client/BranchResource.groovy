package net.nemerosa.ontrack.dsl.client

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.dsl.Branch
import net.nemerosa.ontrack.dsl.Build
import net.nemerosa.ontrack.dsl.Ontrack
import net.nemerosa.ontrack.dsl.PromotionLevel
import net.nemerosa.ontrack.json.JsonUtils

class BranchResource extends AbstractProjectResource implements Branch {

    BranchResource(Ontrack ontrack, JsonNode node) {
        super(ontrack, node)
    }

    @Override
    String getProject() {
        JsonUtils.get(node.path('project'), 'name')
    }

    @Override
    List<Build> filter(String filterType, Map<String, ?> filterConfig) {
        def url = query(
                "${link('view')}/${filterType}",
                filterConfig
        )
        get(url).buildViews.collect { new BuildResource(ontrack, it.build) }
    }

    @Override
    List<Build> standardFilter(Map<String, ?> filterConfig) {
        filter('net.nemerosa.ontrack.service.StandardBuildFilterProvider', filterConfig)
    }

    @Override
    List<Build> getLastPromotedBuilds() {
        filter('net.nemerosa.ontrack.service.PromotionLevelBuildFilterProvider', [:])
    }

    @Override
    PromotionLevel promotionLevel(String name, String description) {
        new PromotionLevelResource(
                ontrack,
                post(link('createPromotionLevel'), [
                        name       : name,
                        description: description
                ])
        )
    }

    @Override
    Build build(String name, String description) {
        new BuildResource(
                ontrack,
                post(link('createBuild'), [
                        name       : name,
                        description: description
                ])
        )
    }
}
