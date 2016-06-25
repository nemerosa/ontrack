package net.nemerosa.ontrack.dsl.properties

import net.nemerosa.ontrack.dsl.Build
import net.nemerosa.ontrack.dsl.Ontrack

class BuildProperties extends ProjectEntityProperties {

    BuildProperties(Ontrack ontrack, Build build) {
        super(ontrack, build)
    }

    /**
     * Sets the Jenkins build property on a build
     */
    def jenkinsBuild(String configuration, String job, int buildNumber) {
        property(
                'net.nemerosa.ontrack.extension.jenkins.JenkinsBuildPropertyType',
                [
                        configuration: configuration,
                        job          : job,
                        build        : buildNumber
                ]
        )
    }

    /**
     * Gets the Jenkins build property
     */
    def getJenkinsBuild() {
        property('net.nemerosa.ontrack.extension.jenkins.JenkinsBuildPropertyType')
    }

    /**
     * Sets the Release property
     */
    def label(String name) {
        property(
                'net.nemerosa.ontrack.extension.general.ReleasePropertyType',
                [
                        name: name
                ]
        )
    }

    /**
     * Gets the Release property
     */
    def getLabel() {
        property('net.nemerosa.ontrack.extension.general.ReleasePropertyType').name
    }

    /**
     * Git commit property
     */

    def gitCommit(String commit) {
        property('net.nemerosa.ontrack.extension.git.property.GitCommitPropertyType', [
                commit: commit
        ])
    }

    def getGitCommit() {
        property('net.nemerosa.ontrack.extension.git.property.GitCommitPropertyType').commit
    }

}
