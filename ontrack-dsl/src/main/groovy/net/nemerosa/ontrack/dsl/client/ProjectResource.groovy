package net.nemerosa.ontrack.dsl.client

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.dsl.Branch
import net.nemerosa.ontrack.dsl.Ontrack
import net.nemerosa.ontrack.dsl.Project
import net.nemerosa.ontrack.dsl.properties.ProjectProperties

class ProjectResource extends AbstractProjectResource implements Project {

    ProjectResource(Ontrack ontrack, JsonNode node) {
        super(ontrack, node)
    }

    @Override
    def call(Closure closure) {
        closure.delegate = this
        closure()
    }

    @Override
    Branch branch(String name) {
        new BranchResource(
                ontrack,
                post(link('createBranch'), [
                        name       : name,
                        description: ''
                ])
        )
    }

    @Override
    Branch branch(String name, Closure closure) {
        def branch = branch(name)
        branch(closure)
        branch
    }

    @Override
    ProjectProperties getProperties() {
        new ProjectProperties(ontrack, this)
    }

    @Override
    def properties(Closure closure) {
        def p = properties
        closure.delegate = p
        closure()
    }
}
