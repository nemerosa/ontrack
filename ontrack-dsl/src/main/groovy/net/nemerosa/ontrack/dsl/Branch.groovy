package net.nemerosa.ontrack.dsl

import net.nemerosa.ontrack.dsl.doc.DSL
import net.nemerosa.ontrack.dsl.doc.DSLMethod
import net.nemerosa.ontrack.dsl.properties.BranchProperties

@DSL
class Branch extends AbstractProjectResource {

    Branch(Ontrack ontrack, Object node) {
        super(ontrack, node)
    }

    @DSLMethod("Returns the name of the project the branch belongs to.")
    String getProject() {
        node?.project?.name
    }

    @DSLMethod("Configures the branch using a closure.")
    def call(Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = this
        closure()
    }

    @DSLMethod("Runs any filter and returns the list of corresponding builds.")
    List<Build> filter(String filterType, Map<String, ?> filterConfig) {
        def url = query(
                "${link('view')}/${filterType}",
                filterConfig
        )
        ontrack.get(url).buildViews.collect { new Build(ontrack, it.build) }
    }

    @DSLMethod("Returns a list of builds for the branch, filtered according to given criteria.")
    List<Build> standardFilter(Map<String, ?> filterConfig) {
        filter('net.nemerosa.ontrack.service.StandardBuildFilterProvider', filterConfig)
    }

    List<Build> intervalFilter(Map<String, ?> filterConfig) {
        filter('net.nemerosa.ontrack.service.BuildIntervalFilterProvider', [
                from: filterConfig.from,
                to  : filterConfig.to,
        ])
    }

    @DSLMethod("Returns the last promoted builds.")
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

    /**
     * Gets the template instance parameters
     */
    TemplateInstance getInstance() {
        def instanceLink = optionalLink('templateInstance')
        if (instanceLink) {
            return new TemplateInstance(
                    ontrack,
                    ontrack.get(instanceLink)
            )
        } else {
            return null
        }
    }

    /**
     * Sync this branch template instance with its template.
     */
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
                manual    : manual,
                parameters: parameters,
        ])
    }

    /**
     * Gets the list of promotion levels for this branch
     */
    List<PromotionLevel> getPromotionLevels() {
        return ontrack.get(link('promotionLevels')).resources.collect { node ->
            new PromotionLevel(ontrack, node)
        }
    }

    PromotionLevel promotionLevel(String name, String description = '', boolean getIfExists = false) {
        def node = ontrack.get(link('promotionLevels')).resources.find { it.name == name }
        if (node) {
            if (getIfExists) {
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

    PromotionLevel promotionLevel(String name, String description = '', boolean getIfExists = false, Closure closure) {
        def pl = promotionLevel(name, description, getIfExists)
        pl(closure)
        pl
    }

    /**
     * Gets the list of validation stamps for this branch
     */
    List<ValidationStamp> getValidationStamps() {
        ontrack.get(link('validationStamps')).resources.collect { node ->
            new ValidationStamp(ontrack, node)
        }
    }

    ValidationStamp validationStamp(String name, String description = '', boolean getIfExists = false) {
        def node = ontrack.get(link('validationStamps')).resources.find { it.name == name }
        if (node) {
            if (getIfExists) {
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

    ValidationStamp validationStamp(String name, String description = '', boolean getIfExists = false, Closure closure) {
        def vs = validationStamp(name, description, getIfExists)
        vs(closure)
        vs
    }

    Build build(String name, String description = '', boolean getIfExists = false) {
        def builds = ontrack.project(this.project).search(branchName: this.name, buildName: name)
        if (builds.empty) {
            new Build(
                    ontrack,
                    ontrack.post(link('createBuild'), [
                            name       : name,
                            description: description
                    ])
            )
        } else if (getIfExists) {
            new Build(
                    ontrack,
                    ontrack.get(builds[0].node._self)
            )
        } else {
            throw new ObjectAlreadyExistsException("Build ${name} already exists.")
        }
    }

    Build build(String name, String description = '', boolean getIfExists = false, Closure closure) {
        def b = build(name, description, getIfExists)
        b(closure)
        b
    }

    /**
     * Download file from the branch SCM
     */
    String download(String path) {
        ontrack.text(query(link('download'), [path: path]))
    }

    @DSLMethod("Access to the branch properties")
    BranchProperties getConfig() {
        new BranchProperties(ontrack, this)
    }

    String getType() {
        node.type
    }
}
