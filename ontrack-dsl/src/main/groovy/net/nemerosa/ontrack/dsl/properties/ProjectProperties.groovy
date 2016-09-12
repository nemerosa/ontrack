package net.nemerosa.ontrack.dsl.properties

import net.nemerosa.ontrack.dsl.Ontrack
import net.nemerosa.ontrack.dsl.Project
import net.nemerosa.ontrack.dsl.PropertyNotFoundException
import net.nemerosa.ontrack.dsl.doc.DSL
import net.nemerosa.ontrack.dsl.doc.DSLMethod
import net.nemerosa.ontrack.dsl.doc.DSLProperties

@DSL
@DSLProperties
class ProjectProperties extends ProjectEntityProperties {

    ProjectProperties(Ontrack ontrack, Project project) {
        super(ontrack, project)
    }

    /**
     * Stale property.
     *
     * Sets the disabling and deleting durations (in days) on the project.
     */
    @DSLMethod(value = "Setup of stale branches management.", count = 2)
    def stale(int disablingDuration = 0, int deletingDuration = 0) {
        assert disablingDuration >= 0: "The disabling duration must be >= 0"
        assert deletingDuration >= 0: "The deleting duration must be >= 0"
        property('net.nemerosa.ontrack.extension.stale.StalePropertyType', [
                disablingDuration: disablingDuration,
                deletingDuration : deletingDuration,
        ])
    }

    /**
     * Gets the stale property
     */
    @DSLMethod(see = "stale")
    def getStale() {
        try {
            return property('net.nemerosa.ontrack.extension.stale.StalePropertyType')
        } catch (PropertyNotFoundException ignored) {
            return [
                    disablingDuration: 0,
                    deletingDuration : 0,
            ]
        }
    }

    /**
     * GitHub property
     * @param name Configuration name
     * @param parameters Map of GitHub parameters, like 'repository' and 'indexationInterval'
     */
    @DSLMethod("Configures the project for GitHub.")
    def gitHub(Map<String, ?> parameters, String name) {
        assert parameters.containsKey('repository'): "The 'repository' parameter is required"
        property('net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationPropertyType',
                parameters + [
                        configuration: name
                ])
    }

    /**
     * SVN configuration
     */

    @DSLMethod("Configures the project for Subversion.")
    def svn(String name, String projectPath) {
        property('net.nemerosa.ontrack.extension.svn.property.SVNProjectConfigurationPropertyType', [
                configuration: name,
                projectPath  : projectPath
        ])
    }

    @DSLMethod(see = "svn")
    def getSvn() {
        property('net.nemerosa.ontrack.extension.svn.property.SVNProjectConfigurationPropertyType')
    }

    /**
     * Git configuration
     */

    @DSLMethod("Configures the project for Git.")
    def git(String name) {
        property('net.nemerosa.ontrack.extension.git.property.GitProjectConfigurationPropertyType', [
                configuration: name,
        ])
    }

    @DSLMethod(see = "git")
    def getGit() {
        property('net.nemerosa.ontrack.extension.git.property.GitProjectConfigurationPropertyType')
    }

    /**
     * Stash configuration
     */

    @DSLMethod
    def stash(String name, String project, String repository) {
        property('net.nemerosa.ontrack.extension.stash.property.StashProjectConfigurationPropertyType', [
                configuration: name,
                project      : project,
                repository   : repository,
        ])
    }

    @DSLMethod(see = "stash")
    def getStash() {
        property('net.nemerosa.ontrack.extension.stash.property.StashProjectConfigurationPropertyType')
    }

    /**
     * JIRA Follow links
     */
    @DSLMethod
    def jiraFollowLinks(String... linkNames) {
        jiraFollowLinks(linkNames as List)
    }

    @DSLMethod(see = "jiraFollowLinks", id = "jiraFollowLinks-collection")
    def jiraFollowLinks(Collection<String> linkNames) {
        property('net.nemerosa.ontrack.extension.jira.JIRAFollowLinksPropertyType', [
                linkNames: linkNames
        ])
    }

    @DSLMethod(see = "jiraFollowLinks")
    List<String> getJiraFollowLinks() {
        property('net.nemerosa.ontrack.extension.jira.JIRAFollowLinksPropertyType').linkNames
    }

    /**
     * Auto validation stamp
     */

    @DSLMethod(count = 2)
    def autoValidationStamp(boolean autoCreate = true, boolean autoCreateIfNotPredefined = false) {
        property('net.nemerosa.ontrack.extension.general.AutoValidationStampPropertyType', [
                autoCreate: autoCreate,
                autoCreateIfNotPredefined: autoCreateIfNotPredefined,
        ])
    }

    @DSLMethod(see = "autoValidationStamp")
    boolean getAutoValidationStamp() {
        property('net.nemerosa.ontrack.extension.general.AutoValidationStampPropertyType')?.autoCreate
    }

    /**
     * Auto promotion level
     */

    @DSLMethod(count = 1)
    def autoPromotionLevel(boolean autoCreate = true) {
        property('net.nemerosa.ontrack.extension.general.AutoPromotionLevelPropertyType', [
                autoCreate: autoCreate
        ])
    }

    @DSLMethod(see = "autoPromotionLevel")
    boolean getAutoPromotionLevel() {
        property('net.nemerosa.ontrack.extension.general.AutoPromotionLevelPropertyType')?.autoCreate
    }

}
