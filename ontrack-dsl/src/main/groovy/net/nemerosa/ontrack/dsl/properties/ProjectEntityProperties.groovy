package net.nemerosa.ontrack.dsl.properties

import net.nemerosa.ontrack.dsl.Ontrack
import net.nemerosa.ontrack.dsl.ProjectEntity

class ProjectEntityProperties {

    private final Ontrack ontrack
    private final ProjectEntity entity

    ProjectEntityProperties(Ontrack ontrack, ProjectEntity entity) {
        this.ontrack = ontrack
        this.entity = entity
    }

    def property(String type, data) {
        entity.property(type, data)
    }

}
