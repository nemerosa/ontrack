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

    def property(String type) {
        entity.property(type)
    }

    /**
     * Links
     */
    def links(Map<String, String> links) {
        property('net.nemerosa.ontrack.extension.general.LinkPropertyType', [
                links: links.collect { k, v ->
                    [
                            name : k,
                            value: v,
                    ]
                }
        ])
    }

    Map<String, String> getLinks() {
        property('net.nemerosa.ontrack.extension.general.LinkPropertyType').links.collectEntries {
            [it.name, it.value]
        }
    }

    /**
     * Jenkins build
     */

    def jenkinsBuild(String configuration, String job, int build) {
        property('net.nemerosa.ontrack.extension.jenkins.JenkinsBuildPropertyType', [
                configuration: configuration,
                job          : job,
                build        : build,
        ])
    }

    def getJenkinsBuild() {
        property('net.nemerosa.ontrack.extension.jenkins.JenkinsBuildPropertyType')
    }

}
