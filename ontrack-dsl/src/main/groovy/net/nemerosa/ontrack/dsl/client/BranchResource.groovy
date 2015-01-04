package net.nemerosa.ontrack.dsl.client

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.dsl.*
import net.nemerosa.ontrack.dsl.properties.BranchProperties
import net.nemerosa.ontrack.dsl.support.BranchTemplateDefinition
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
        closure.resolveStrategy = Closure.DELEGATE_FIRST
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
    def template(Closure closure) {
        def definition = new BranchTemplateDefinition()
        // Configuration
        closure.delegate = definition
        closure()
        // When configured, send the template info
        put(link('templateDefinition'), definition.data)
    }

    @Override
    def sync() {
        post(link('templateSync'), [:])
    }

    @Override
    Branch instance(String sourceName, Map<String, String> params) {
        new BranchResource(
                ontrack,
                put(link('templateInstanceCreate'), [
                        name      : sourceName,
                        manual    : (!params.empty),
                        parameters: params
                ])
        )
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
    PromotionLevel promotionLevel(String name, String description, Closure closure) {
        def pl = promotionLevel(name, description)
        pl(closure)
        pl
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
