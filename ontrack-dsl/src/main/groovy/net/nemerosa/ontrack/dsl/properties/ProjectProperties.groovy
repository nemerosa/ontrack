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
    @DSLMethod(value = "Setup of stale branches management.", count = 3)
    def stale(int disablingDuration = 0, int deletingDuration = 0, List<String> promotionsToKeep = []) {
        assert disablingDuration >= 0: "The disabling duration must be >= 0"
        assert deletingDuration >= 0: "The deleting duration must be >= 0"
        property('net.nemerosa.ontrack.extension.stale.StalePropertyType', [
                disablingDuration: disablingDuration,
                deletingDuration : deletingDuration,
                promotionsToKeep : promotionsToKeep,
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
                    promotionsToKeep : [],
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
     * GitLab configuration
     */

    @DSLMethod
    def gitLab(Map<String, ?> parameters, String name) {
        assert parameters.containsKey('repository'): "The 'repository' parameter is required"
        property('net.nemerosa.ontrack.extension.gitlab.property.GitLabProjectConfigurationPropertyType',
                parameters + [
                        configuration: name
                ])
    }

    @DSLMethod(see = "gitLab")
    def getGitLab() {
        property('net.nemerosa.ontrack.extension.gitlab.property.GitLabProjectConfigurationPropertyType', false)
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

    @DSLMethod(count = 5)
    def stash(String name, String project, String repository, int indexationInterval = 0, String issueServiceConfigurationIdentifier = '') {
        property('net.nemerosa.ontrack.extension.stash.property.StashProjectConfigurationPropertyType', [
                configuration                      : name,
                project                            : project,
                repository                         : repository,
                indexationInterval                 : indexationInterval,
                issueServiceConfigurationIdentifier: issueServiceConfigurationIdentifier,
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
                autoCreate               : autoCreate,
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

    /**
     * Build link display options
     */

    @DSLMethod("Sets the display options for the build links targeting this project.")
    def buildLinkDisplayOptions(boolean useLabel) {
        property('net.nemerosa.ontrack.extension.general.BuildLinkDisplayPropertyType', [
                useLabel: useLabel
        ])
    }

    @DSLMethod(see = "buildLinkDisplayOptions")
    boolean getBuildLinkDisplayOptions() {
        property('net.nemerosa.ontrack.extension.general.BuildLinkDisplayPropertyType')
    }

    /**
     * Main build links properties
     */

    @DSLMethod("Sets the options for displaying the builds being used by the builds of this project.")
    void setMainBuildLinks(MainBuildLinks mainBuildLinks) {
        property("net.nemerosa.ontrack.extension.general.MainBuildLinksProjectPropertyType", [
                labels        : mainBuildLinks.labels,
                overrideGlobal: mainBuildLinks.overrideGlobal,
        ])
    }

    @DSLMethod("Gets the options for displaying the builds being used by the builds of this project.")
    MainBuildLinks getMainBuildLinks() {
        try {
            def property = property("net.nemerosa.ontrack.extension.general.MainBuildLinksProjectPropertyType")
            return new MainBuildLinks(
                    property.labels,
                    property.overrideGlobal
            )
        } catch (PropertyNotFoundException ignored) {
            return null
        }
    }

    /**
     * SonarQube property
     */

    @DSLMethod(value = "Sets the SonarQube settings for this project.")
    void sonarQube(Map<String, ?> values) {
        property("net.nemerosa.ontrack.extension.sonarqube.property.SonarQubePropertyType", values)
    }

    @DSLMethod(see = "sonarQube")
    Map<String, ?> getSonarQube() {
        try {
            return property('net.nemerosa.ontrack.extension.sonarqube.property.SonarQubePropertyType') as Map
        } catch (PropertyNotFoundException ignored) {
            return null
        }
    }

}
