package net.nemerosa.ontrack.dsl.properties

import net.nemerosa.ontrack.dsl.Build
import net.nemerosa.ontrack.dsl.Ontrack
import net.nemerosa.ontrack.dsl.doc.DSL
import net.nemerosa.ontrack.dsl.doc.DSLProperties

@DSL
@DSLProperties
class BuildProperties extends ProjectEntityProperties {

    BuildProperties(Ontrack ontrack, Build build) {
        super(ontrack, build)
    }

    @DSL("Associates a Jenkins build with this build.")
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

    @DSL("Gets the Jenkins build property.")
    def getJenkinsBuild() {
        property('net.nemerosa.ontrack.extension.jenkins.JenkinsBuildPropertyType')
    }

    @DSL("Sets the Release property.")
    def label(String name) {
        property(
                'net.nemerosa.ontrack.extension.general.ReleasePropertyType',
                [
                        name: name
                ]
        )
    }

    @DSL("Gets the release name associated with this build.")
    def getLabel() {
        property('net.nemerosa.ontrack.extension.general.ReleasePropertyType').name
    }

    /**
     * Git commit property
     */
    @DSL("Sets a Git commmit associated to this build.")
    def gitCommit(String commit) {
        property('net.nemerosa.ontrack.extension.git.property.GitCommitPropertyType', [
                commit: commit
        ])
    }

    @DSL("Gets the Git commit associated to this build.")
    def getGitCommit() {
        property('net.nemerosa.ontrack.extension.git.property.GitCommitPropertyType').commit
    }

}
