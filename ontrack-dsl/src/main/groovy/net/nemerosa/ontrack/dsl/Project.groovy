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

    Branch branch(String name, String description = '', boolean updateIfExists = false) {
        def node = ontrack.get(link('branches')).resources.find { it.name == name }
        if (node) {
            if (updateIfExists) {
                new Branch(
                        ontrack,
                        ontrack.get(node._self)
                )
            } else {
                throw new ObjectAlreadyExistsException("Branch ${name} already exists.")
            }
        } else {
            new Branch(
                    ontrack,
                    ontrack.post(link('createBranch'), [
                            name       : name,
                            description: description
                    ])
            )
        }
    }

    Branch branch(String name, String description = '', boolean updateIfExists = false, Closure closure) {
        Branch b = branch(name, description, updateIfExists)
        b(closure)
        b
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
