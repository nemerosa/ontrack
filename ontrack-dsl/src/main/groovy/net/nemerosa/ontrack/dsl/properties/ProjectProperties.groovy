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
     * @param parameters Map of GitHub parameters, like 'repository' and 'indexationInterval'
     */
    def gitHub(Map<String, ?> parameters, String name) {
        assert parameters.containsKey('repository') : "The 'repository' parameter is required"
        property('net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationPropertyType',
                parameters + [
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
     * Stash configuration
     */

    def stash(String name, String project, String repository) {
        property('net.nemerosa.ontrack.extension.stash.property.StashProjectConfigurationPropertyType', [
                configuration: name,
                project      : project,
                repository   : repository,
        ])
    }

    def getStash() {
        property('net.nemerosa.ontrack.extension.stash.property.StashProjectConfigurationPropertyType')
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

    /**
     * Auto validation stamp
     */

    def autoValidationStamp(boolean autoCreate = true) {
        property('net.nemerosa.ontrack.boot.properties.AutoValidationStampPropertyType', [
                autoCreate: autoCreate
        ])
    }

    boolean getAutoValidationStamp() {
        property('net.nemerosa.ontrack.boot.properties.AutoValidationStampPropertyType')?.autoCreate
    }

}
