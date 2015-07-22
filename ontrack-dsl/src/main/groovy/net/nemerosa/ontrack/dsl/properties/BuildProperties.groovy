package net.nemerosa.ontrack.dsl.properties

import net.nemerosa.ontrack.dsl.Build
import net.nemerosa.ontrack.dsl.BuildLink
import net.nemerosa.ontrack.dsl.Ontrack
import net.nemerosa.ontrack.dsl.PropertyNotFoundException

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
     * Build links properties
     */

    def buildLink(String project, String build) {
        // Gets the existing list of links
        def links = buildLinks.collect { [project: it.project, build: it.build] }
        // Adds the link
        links.add([project: project, build: build])
        // Edits the property
        property('net.nemerosa.ontrack.extension.general.BuildLinkPropertyType', [
                links: links
        ])
    }

    List<BuildLink> getBuildLinks() {
        try {
            return property('net.nemerosa.ontrack.extension.general.BuildLinkPropertyType').links.collect {
                new BuildLink(ontrack, it)
            }
        } catch (PropertyNotFoundException ignored) {
            return []
        }
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
