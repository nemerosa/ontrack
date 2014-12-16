package net.nemerosa.ontrack.dsl.client

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.dsl.Ontrack
import net.nemerosa.ontrack.dsl.Project

class ProjectResource extends AbstractProjectResource implements Project {

    ProjectResource(Ontrack ontrack, JsonNode node) {
        super(ontrack, node)
    }

    @Override
    def call(Closure closure) {
        closure.delegate = this
        closure()
    }
}
