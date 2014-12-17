package net.nemerosa.ontrack.dsl.client

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.dsl.*
import net.nemerosa.ontrack.dsl.properties.BranchProperties
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
    def call(Closure closure) {
        closure.delegate = this
        closure()
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
    ValidationStamp validationStamp(String name, String description) {
        new ValidationStampResource(
                ontrack,
                post(link('createValidationStamp'), [
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

    @Override
    BranchProperties getProperties() {
        new BranchProperties(ontrack, this)
    }
}
