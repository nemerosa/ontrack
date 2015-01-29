package net.nemerosa.ontrack.dsl

import net.nemerosa.ontrack.dsl.properties.ProjectProperties

class Project extends AbstractProjectResource {

    Project(Ontrack ontrack, Object node) {
        super(ontrack, node)
    }

    def call(Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = this
        closure()
    }

    Branch branch(String name) {
        new Branch(
                ontrack,
                ontrack.post(link('createBranch'), [
                        name       : name,
                        description: ''
                ])
        )
    }

    Branch branch(String name, Closure closure) {
        def branch = branch(name)
        branch(closure)
        branch
    }

    ProjectProperties getConfig() {
        new ProjectProperties(ontrack, this)
    }

    List<Build> search(Map<String, ?> form) {
        def url = query(
                "${link('buildSearch')}/search",
                form
        )
        ontrack.get(url).resources.collect { new Build(ontrack, it.build) }
    }


}
