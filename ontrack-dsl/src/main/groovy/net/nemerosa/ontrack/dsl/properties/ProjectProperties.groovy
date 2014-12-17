package net.nemerosa.ontrack.dsl.properties

import net.nemerosa.ontrack.dsl.Ontrack
import net.nemerosa.ontrack.dsl.Project

class ProjectProperties {

    private final Ontrack ontrack
    private final Project project

    ProjectProperties(Ontrack ontrack, Project project) {
        this.ontrack = ontrack
        this.project = project
    }

    /**
     * GitHub property
     * @param name Configuration name
     */
    def gitHub(String name) {
        project.property(
                'net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationPropertyType',
                [
                        configuration: name
                ]
        )
    }
}
