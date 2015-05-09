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
        ontrack.get(url).buildViews.collect { new Build(ontrack, it.build) }
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
        ontrack.put(link('templateDefinition'), definition.data)
    }

    def sync() {
        ontrack.post(link('templateSync'), [:])
    }

    def syncInstance() {
        ontrack.post(link('templateInstanceSync'), [:])
    }

    Branch instance(String sourceName, Map<String, String> params) {
        new Branch(
                ontrack,
                ontrack.put(link('templateInstanceCreate'), [
                        name      : sourceName,
                        manual    : (!params.empty),
                        parameters: params
                ])
        )
    }

    def unlink() {
        ontrack.delete(link('templateInstanceDisconnect'))
    }

    def link(String templateName, boolean manual = true, Map<String, String> parameters) {
        // Gets the template id
        def templateId = ontrack.branch(project, templateName).id
        // Sends the request
        ontrack.post(link('templateInstanceConnect'), [
                templateId: templateId,
                manual: manual,
                parameters: parameters,
        ])
    }

    PromotionLevel promotionLevel(String name, String description = '', boolean updateIfExists = false) {
        def node = ontrack.get(link('promotionLevels')).resources.find { it.name == name }
        if (node) {
            if (updateIfExists) {
                new PromotionLevel(
                        ontrack,
                        ontrack.get(node._self)
                )
            } else {
                throw new ObjectAlreadyExistsException("Promotion level ${name} already exists.")
            }
        } else {
            new PromotionLevel(
                    ontrack,
                    ontrack.post(link('createPromotionLevel'), [
                            name       : name,
                            description: description
                    ])
            )
        }
    }

    PromotionLevel promotionLevel(String name, String description = '', boolean updateIfExists = false, Closure closure) {
        def pl = promotionLevel(name, description, updateIfExists)
        pl(closure)
        pl
    }

    ValidationStamp validationStamp(String name, String description = '', boolean updateIfExists = false) {
        def node = ontrack.get(link('validationStamps')).resources.find { it.name == name }
        if (node) {
            if (updateIfExists) {
                new ValidationStamp(
                        ontrack,
                        ontrack.get(node._self)
                )
            } else {
                throw new ObjectAlreadyExistsException("Validation stamp ${name} already exists.")
            }
        } else {
            new ValidationStamp(
                    ontrack,
                    ontrack.post(link('createValidationStamp'), [
                            name       : name,
                            description: description
                    ])
            )
        }
    }

    ValidationStamp validationStamp(String name, String description = '', boolean updateIfExists = false, Closure closure) {
        def vs = validationStamp(name, description, updateIfExists)
        vs(closure)
        vs
    }

    Build build(String name, String description = '', boolean updateIfExists = false) {
        def builds = ontrack.project(this.project).search(branchName: this.name, buildName: name)
        if (builds.empty) {
            new Build(
                    ontrack,
                    ontrack.post(link('createBuild'), [
                            name       : name,
                            description: description
                    ])
            )
        } else if (updateIfExists) {
            new Build(
                    ontrack,
                    ontrack.get(builds[0].node._self)
            )
        } else {
            throw new ObjectAlreadyExistsException("Build ${name} already exists.")
        }
    }

    Build build(String name, String description = '', boolean updateIfExists = false, Closure closure) {
        def b = build(name, description, updateIfExists)
        b(closure)
        b
    }

    BranchProperties getConfig() {
        new BranchProperties(ontrack, this)
    }

    String getType() {
        node.type
    }
}
