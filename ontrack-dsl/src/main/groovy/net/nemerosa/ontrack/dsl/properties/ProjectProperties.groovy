package net.nemerosa.ontrack.dsl.properties

import net.nemerosa.ontrack.dsl.Ontrack
import net.nemerosa.ontrack.dsl.Project

class ProjectProperties extends ProjectEntityProperties {

    ProjectProperties(Ontrack ontrack, Project project) {
        super(ontrack, project)
    }

    /**
     * GitHub property
     * @param name Configuration name
     */
    def gitHub(String name) {
        property(
                'net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationPropertyType',
                [
                        configuration: name
                ]
        )
    }

    /**
     * SVN configuration
     */

    def svn (String name, String projectPath) {
        property('net.nemerosa.ontrack.extension.svn.property.SVNProjectConfigurationPropertyType', [
                configuration: name,
                projectPath: projectPath
        ])
    }

    def getSvn() {
        property('net.nemerosa.ontrack.extension.svn.property.SVNProjectConfigurationPropertyType')
    }
}
