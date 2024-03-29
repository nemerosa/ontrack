package net.nemerosa.ontrack.dsl.v4

import net.nemerosa.ontrack.dsl.v4.doc.DSL
import net.nemerosa.ontrack.dsl.v4.doc.DSLMethod
import net.nemerosa.ontrack.dsl.v4.http.OTNotFoundException
import net.nemerosa.ontrack.dsl.v4.properties.BranchProperties

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

    @DSLMethod("Returns the last promoted builds.")
    List<Build> getLastPromotedBuilds() {
        filter('net.nemerosa.ontrack.service.PromotionLevelBuildFilterProvider', [:])
    }

    /**
     * Gets the list of promotion levels for this branch
     */
    @DSLMethod("Gets the list of promotion levels for this branch.")
    List<PromotionLevel> getPromotionLevels() {
        return ontrack.get(link('promotionLevels')).resources.collect { node ->
            new PromotionLevel(ontrack, node)
        }
    }

    @DSLMethod(value = "Creates a promotion level for this branch.", count = 3)
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

    @DSLMethod(value = "Creates a promotion level for this branch and configures it using a closure.", count = 4, id = "promotionLevel-closure")
    PromotionLevel promotionLevel(String name, String description = '', boolean getIfExists = false, Closure closure) {
        def pl = promotionLevel(name, description, getIfExists)
        pl(closure)
        pl
    }

    /**
     * Gets the list of validation stamps for this branch
     */
    @DSLMethod("Gets the list of validation stamps for this branch.")
    List<ValidationStamp> getValidationStamps() {
        ontrack.get(link('validationStamps')).resources.collect { node ->
            new ValidationStamp(ontrack, node)
        }
    }

    @DSLMethod(value = "Creates a validation stamp for this branch.", count = 3)
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

    @DSLMethod(value = "Creates a validation stamp for this branch and configures it using a closure.", count = 4, id = "validationStamp-closure")
    ValidationStamp validationStamp(String name, String description = '', boolean getIfExists = false, Closure closure) {
        def vs = validationStamp(name, description, getIfExists)
        vs(closure)
        vs
    }

    @DSLMethod(value = "Creates a build for the branch", count = 3)
    Build build(String name, String description = '', boolean getIfExists = false) {
        try {
            def existing = new Build(
                    ontrack,
                    ontrack.get("rest/structure/branches/${id}/builds/${name}")
            )
            if (getIfExists) {
                return existing
            } else {
                throw new ObjectAlreadyExistsException("Build ${name} already exists.")
            }
        } catch (OTNotFoundException ignored) {
            // Creating the build
            return new Build(
                    ontrack,
                    ontrack.post(link('createBuild'), [
                            name       : name,
                            description: description
                    ])
            )
        }
    }

    @DSLMethod(value = "Creates a build for the branch and configures it using a closure. See <<dsl-branch-build,`build`>>.", count = 4, id = "build-closure")
    Build build(String name, String description = '', boolean getIfExists = false, Closure closure) {
        def b = build(name, description, getIfExists)
        b(closure)
        b
    }

    /**
     * Download file from the branch SCM
     */
    @DSLMethod
    String download(String path) {
        ontrack.text(query(link('download'), [path: path]))
    }

    @DSLMethod("Access to the branch properties")
    BranchProperties getConfig() {
        new BranchProperties(ontrack, this)
    }

    @DSLMethod("Gets the disabled state of the branch")
    boolean isDisabled() {
        return node.disabled as boolean
    }

    @DSLMethod("Disables the branch")
    def disable() {
        ontrack.put(link('disable'), null)
    }

    @DSLMethod("Enables the branch")
    def enable() {
        ontrack.put(link('enable'), null)
    }

}
