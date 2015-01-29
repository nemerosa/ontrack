package net.nemerosa.ontrack.dsl

import net.nemerosa.ontrack.dsl.properties.BranchProperties

class Branch extends AbstractProjectResource {

    Branch(Ontrack ontrack, Object node) {
        super(ontrack, node)
    }

    String getProject() {
        node?.project?.name
    }

    def call(Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = this
        closure()
    }

    List<Build> filter(String filterType, Map<String, ?> filterConfig) {
        def url = query(
                "${link('view')}/${filterType}",
                filterConfig
        )
        get(url).buildViews.collect { new Build(ontrack, it.build) }
    }

    List<Build> standardFilter(Map<String, ?> filterConfig) {
        filter('net.nemerosa.ontrack.service.StandardBuildFilterProvider', filterConfig)
    }

    List<Build> getLastPromotedBuilds() {
        filter('net.nemerosa.ontrack.service.PromotionLevelBuildFilterProvider', [:])
    }

    def template(Closure closure) {
        def definition = new BranchTemplateDefinition()
        // Configuration
        closure.delegate = definition
        closure()
        // When configured, send the template info
        put(link('templateDefinition'), definition.data)
    }

    def sync() {
        post(link('templateSync'), [:])
    }

    Branch instance(String sourceName, Map<String, String> params) {
        new Branch(
                ontrack,
                put(link('templateInstanceCreate'), [
                        name      : sourceName,
                        manual    : (!params.empty),
                        parameters: params
                ])
        )
    }

    PromotionLevel promotionLevel(String name, String description = '') {
        new PromotionLevel(
                ontrack,
                post(link('createPromotionLevel'), [
                        name       : name,
                        description: description
                ])
        )
    }

    PromotionLevel promotionLevel(String name, String description = '', Closure closure) {
        def pl = promotionLevel(name, description)
        pl(closure)
        pl
    }

    ValidationStamp validationStamp(String name, String description = '') {
        new ValidationStamp(
                ontrack,
                post(link('createValidationStamp'), [
                        name       : name,
                        description: description
                ])
        )
    }

    ValidationStamp validationStamp(String name, String description = '', Closure closure) {
        def vs = validationStamp(name, description)
        vs(closure)
        vs
    }

    Build build(String name, String description = '') {
        new Build(
                ontrack,
                post(link('createBuild'), [
                        name       : name,
                        description: description
                ])
        )
    }

    BranchProperties getConfig() {
        new BranchProperties(ontrack, this)
    }
}
