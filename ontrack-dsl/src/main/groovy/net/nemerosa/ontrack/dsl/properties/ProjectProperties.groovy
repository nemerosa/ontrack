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
        property('net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationPropertyType', [
                configuration: name
        ])
    }

    /**
     * SVN configuration
     */

    def svn(String name, String projectPath) {
        property('net.nemerosa.ontrack.extension.svn.property.SVNProjectConfigurationPropertyType', [
                configuration: name,
                projectPath  : projectPath
        ])
    }

    def getSvn() {
        property('net.nemerosa.ontrack.extension.svn.property.SVNProjectConfigurationPropertyType')
    }

    /**
     * Git configuration
     */

    def git(String name) {
        property('net.nemerosa.ontrack.extension.git.property.GitProjectConfigurationPropertyType', [
                configuration: name,
        ])
    }

    def getGit() {
        property('net.nemerosa.ontrack.extension.git.property.GitProjectConfigurationPropertyType')
    }

    /**
     * JIRA Follow links
     */
    def jiraFollowLinks(String... linkNames) {
        jiraFollowLinks(linkNames as List)
    }

    def jiraFollowLinks(Collection<String> linkNames) {
        property('net.nemerosa.ontrack.extension.jira.JIRAFollowLinksPropertyType', [
                linkNames: linkNames
        ])
    }

    List<String> getJiraFollowLinks() {
        property('net.nemerosa.ontrack.extension.jira.JIRAFollowLinksPropertyType').linkNames
    }

}
