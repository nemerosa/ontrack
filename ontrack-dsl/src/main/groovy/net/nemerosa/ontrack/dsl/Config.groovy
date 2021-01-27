package net.nemerosa.ontrack.dsl

import net.nemerosa.ontrack.dsl.doc.DSL
import net.nemerosa.ontrack.dsl.doc.DSLMethod

@DSL(value = "General configuration of Ontrack.")
class Config {

    private final Ontrack ontrack

    Config(Ontrack ontrack) {
        this.ontrack = ontrack
    }

    @DSLMethod("Checks if the projects are accessible in anonymous mode.")
    boolean getGrantProjectViewToAll() {
        def settings = ontrack.get('rest/settings/general-security')
        return settings.grantProjectViewToAll
    }

    /**
     * Updates security settings
     */
    @DSLMethod("Sets if the projects are accessible in anonymous mode.")
    def setGrantProjectViewToAll(boolean grantProjectViewToAll) {
        ontrack.put(
                'rest/settings/general-security',
                [
                        grantProjectViewToAll: grantProjectViewToAll
                ]
        )
    }

    /**
     * Management of GitLab configurations
     */

    @DSLMethod
    def gitLab(Map<String, ?> parameters, String name) {
        def params = parameters + [name: name]
        ontrack.post(
                'extension/gitlab/configurations/create',
                params
        )
    }

    @DSLMethod(see = "gitLab")
    List<String> getGitLab() {
        ontrack.get('extension/gitlab/configurations/descriptors').resources.collect { it.id }
    }

    /**
     * Creates or update a GitHub configuration.
     */

    @DSLMethod(id = "github")
    def gitHub(String name) {
        gitHub([:], name)
    }

    @DSLMethod(see = "github", id = "github-name")
    def gitHub(Map<String, ?> parameters, String name) {
        def params = parameters + [name: name]
        ontrack.post(
                'extension/github/configurations/create',
                params
        )
    }

    /**
     * Gets the list of all GitHub configuration names
     */
    @DSLMethod(see = "github")
    List<String> getGitHub() {
        ontrack.get('extension/github/configurations/descriptors').resources.collect { it.id }
    }

    /**
     * Stash configurations.
     */

    @DSLMethod("Creates or updates a Bitbucket configuration.")
    def stash(Map<String, ?> parameters, String name) {
        def params = parameters + [name: name]
        ontrack.post(
                'extension/stash/configurations/create',
                params
        )
    }

    @DSLMethod(see = "stash")
    List<String> getStash() {
        ontrack.get('extension/stash/configurations/descriptors').resources.collect { it.id }
    }

    /**
     * Creates or update a Git configuration
     */
    @DSLMethod("Creates or update a Git configuration")
    def git(Map<String, ?> parameters, String name) {
        def params = parameters + [name: name]
        ontrack.post(
                'extension/git/configurations/create',
                params
        )
    }

    /**
     * Gets the list of all Git configuration names
     */
    @DSLMethod(see = "git")
    List<String> getGit() {
        ontrack.get('extension/git/configurations/descriptors').resources.collect { it.id }
    }

    /**
     * Jenkins configuration
     */

    @DSLMethod(value = "Creates or updates a Jenkins configuration.", count = 4)
    def jenkins(String name, String url, String user = '', String password = '') {
        ontrack.post(
                'extension/jenkins/configurations/create', [
                name    : name,
                url     : url,
                user    : user,
                password: password
        ])
    }

    @DSLMethod(see = "jenkins")
    List<String> getJenkins() {
        ontrack.get('extension/jenkins/configurations/descriptors').resources.collect { it.id }
    }

    /**
     * JIRA configuration
     */

    @DSLMethod(value = "Creates or updates a JIRA configuration.", count = 4)
    def jira(String name, String url, String user = '', String password = '') {
        ontrack.post(
                'extension/jira/configurations/create', [
                name    : name,
                url     : url,
                user    : user,
                password: password
        ])
    }

    @DSLMethod(see = "jira")
    List<String> getJira() {
        ontrack.get('extension/jira/configurations/descriptors').resources.collect { it.id }
    }

    /**
     * Artifactory configuration
     */

    @DSLMethod(value = "Creates or updates a Artifactory configuration.", count = 4)
    def artifactory(String name, String url, String user = '', String password = '') {
        ontrack.post(
                'extension/artifactory/configurations/create', [
                name    : name,
                url     : url,
                user    : user,
                password: password
        ])
    }

    @DSLMethod(see = "artifactory")
    List<String> getArtifactory() {
        ontrack.get('extension/artifactory/configurations/descriptors').resources.collect { it.id }
    }

    /**
     * Predefined validation stamps
     */

    @DSLMethod("Gets the list of validation stamps. See <<dsl-projectproperties-autoValidationStamp,`autoValidationStamp`>>.")
    List<PredefinedValidationStamp> getPredefinedValidationStamps() {
        ontrack.get('rest/admin/predefinedValidationStamps').resources.collect {
            new PredefinedValidationStamp(
                    ontrack,
                    it
            )
        }
    }

    @DSLMethod(value = "See <<dsl-projectproperties-autoValidationStamp,`autoValidationStamp`>>.", count = 3)
    PredefinedValidationStamp predefinedValidationStamp(String name, String description = '', boolean getIfExists = false) {
        def vs = predefinedValidationStamps.find { it.name == name }
        if (vs) {
            if (getIfExists) {
                new PredefinedValidationStamp(
                        ontrack,
                        ontrack.get(vs.link('self'))
                )
            } else {
                throw new ObjectAlreadyExistsException("Predefined validation stamp ${name} already exists.")
            }
        } else {
            new PredefinedValidationStamp(
                    ontrack,
                    ontrack.post(ontrack.get('rest/admin/predefinedValidationStamps')._create, [
                            name       : name,
                            description: description
                    ])
            )
        }
    }

    @DSLMethod(value = "See <<dsl-projectproperties-autoValidationStamp,`autoValidationStamp`>>.", count = 4, id = "predefinedValidationStamp-config")
    PredefinedValidationStamp predefinedValidationStamp(String name, String description = '', boolean getIfExists = false, Closure closure) {
        def vs = predefinedValidationStamp(name, description, getIfExists)
        vs(closure)
        vs
    }

    /**
     * Predefined promotion levels
     */

    @DSLMethod("Gets the list of promotion levels. See <<dsl-projectproperties-autoPromotionLevel,`autoPromotionLevel`>>.")
    List<PredefinedPromotionLevel> getPredefinedPromotionLevels() {
        ontrack.get('rest/admin/predefinedPromotionLevels').resources.collect {
            new PredefinedPromotionLevel(
                    ontrack,
                    it
            )
        }
    }

    @DSLMethod(value = "See <<dsl-projectproperties-autoPromotionLevel,autoPromotionLevel>>.", count = 3)
    PredefinedPromotionLevel predefinedPromotionLevel(String name, String description = '', boolean getIfExists = false) {
        def pl = predefinedPromotionLevels.find { it.name == name }
        if (pl) {
            if (getIfExists) {
                new PredefinedPromotionLevel(
                        ontrack,
                        ontrack.get(pl.link('self'))
                )
            } else {
                throw new ObjectAlreadyExistsException("Predefined promotion level ${name} already exists.")
            }
        } else {
            new PredefinedPromotionLevel(
                    ontrack,
                    ontrack.post(ontrack.get('rest/admin/predefinedPromotionLevels')._create, [
                            name       : name,
                            description: description
                    ])
            )
        }
    }

    @DSLMethod(value = "See <<dsl-projectproperties-autoPromotionLevel,`autoPromotionLevel`>>.", count = 4, id = "predefinedPromotionLevel-config")
    PredefinedPromotionLevel predefinedPromotionLevel(String name, String description = '', boolean getIfExists = false, Closure closure) {
        def vs = predefinedPromotionLevel(name, description, getIfExists)
        vs(closure)
        vs
    }

    /**
     * LDAP settings
     */

    @DSLMethod("Gets the global LDAP settings")
    LDAPSettings getLdapSettings() {
        def json = ontrack.get('rest/settings/ldap').data
        return new LDAPSettings(json as Map)
    }

    @DSLMethod("Sets the global LDAP settings")
    def setLdapSettings(LDAPSettings settings) {
        ontrack.put('rest/settings/ldap', settings)
    }

    /**
     * Gets an existing label
     */
    @DSLMethod(count = 2, value = "Gets an existing label or returns null")
    Label getLabel(String category, String name) {
        def result = ontrack.graphQLQuery(
                '''
                    query LabelSearch($category:String,$name:String) {
                        labels(category: $category, name: $name) {
                            id
                            category
                            name
                            description
                            color
                        }
                    }
                ''',
                [
                        category: category,
                        name    : name
                ]
        )
        def labels = result.data.labels
        if (labels.size > 0) {
            return new Label(ontrack, labels.first())
        } else {
            return null
        }
    }

    /**
     * Gets the list of labels
     */
    @DSLMethod("Gets the list of labels")
    List<Label> getLabels() {
        def result = ontrack.graphQLQuery(
                '''
                    query AllLabels {
                        labels {
                            id
                            category
                            name
                            description
                            color
                        }
                    }
                ''',
                [:]
        )
        return result.data.labels.collect {
            new Label(ontrack, it)
        }
    }

    /**
     * Creating a label
     */
    @DSLMethod(count = 4, value = "Creates or updates a label")
    Label label(String category, String name, String description = "", String color = "#000000") {
        // Gets the existing label
        Label existing = getLabel(category, name)
        if (existing) {
            def result = ontrack.put("rest/labels/${existing.id}/update", [
                    category   : category,
                    name       : name,
                    description: description,
                    color      : color
            ])
            return new Label(ontrack, result)
        } else {
            def result = ontrack.post("rest/labels/create", [
                    category   : category,
                    name       : name,
                    description: description,
                    color      : color
            ])
            return new Label(ontrack, result)
        }
    }

    /**
     * Main build links settings
     */

    @DSLMethod("Gets the main build links settings")
    List<String> getMainBuildLinks() {
        def json = ontrack.get('rest/settings/main-build-links').data
        return json.labels as List<String>
    }

    @DSLMethod("Sets the main build links settings")
    void setMainBuildLinks(List<String> labels) {
        ontrack.put('rest/settings/main-build-links', [
                labels: labels
        ])
    }

    /**
     * Previous promotion condition
     */

    @DSLMethod("Gets the previous promotion condition settings")
    boolean getPreviousPromotionRequired() {
        def json = ontrack.get('rest/settings/previous-promotion-condition')
        return json.previousPromotionRequired as boolean
    }

    @DSLMethod("Sets the previous promotion condition settings")
    void setPreviousPromotionRequired(boolean previousPromotionRequired) {
        ontrack.put('rest/settings/previous-promotion-condition', [
                previousPromotionRequired: previousPromotionRequired
        ])
    }

    /**
     * SonarQube extension
     */

    @DSLMethod(value = "Creates or updates a SonarQube configuration.", count = 4)
    def sonarQube(String name, String url, String user = '', String password = '') {
        ontrack.post(
                'extension/sonarqube/configurations/create', [
                name    : name,
                url     : url,
                user    : user,
                password: password
        ])
    }

    @DSLMethod("Gets the list of SonarQube configuration ids")
    List<String> getSonarQube() {
        ontrack.get('extension/sonarqube/configurations/descriptors').resources.collect { it.id }
    }

    @DSLMethod("Gets the global SonarQube settings")
    SonarQubeMeasuresSettings getSonarQubeSettings() {
        def json = ontrack.get('rest/settings/sonarqube-measures')
        return new SonarQubeMeasuresSettings(json.measures as List<String>, json.disabled as boolean)
    }

    @DSLMethod("Sets the global SonarQube settings")
    def setSonarQubeSettings(SonarQubeMeasuresSettings settings) {
        ontrack.put('rest/settings/sonarqube-measures', settings)
    }

    @DSLMethod("Configuration of the OIDC settings")
    OidcSettings getOidcSettings() {
        return new OidcSettings(ontrack)
    }
}
