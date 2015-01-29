package net.nemerosa.ontrack.dsl

class Config {

    private final Ontrack ontrack

    Config(Ontrack ontrack) {
        this.ontrack = ontrack
    }

    /**
     * Creates or update a GitHub configuration.
     */
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
    List<String> getGitHub() {
        ontrack.get('extension/github/configurations/descriptors').resources.collect { it.id }
    }

    /**
     * Creates or update a Git configuration
     */
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
    List<String> getGit() {
        ontrack.get('extension/git/configurations/descriptors').resources.collect { it.id }
    }

    def svn(Map<String, ?> parameters, String name) {
        def params = parameters + [name: name]
        ontrack.post(
                'extension/svn/configurations/create',
                params
        )
    }

    def getSvn() {
        ontrack.get('extension/svn/configurations/descriptors').resources.collect { it.id }
    }

    /**
     * Jenkins configuration
     */

    def jenkins(String name, String url, String user = '', String password = '') {
        ontrack.post(
                'extension/jenkins/configurations/create', [
                name    : name,
                url     : url,
                user    : user,
                password: password
        ])
    }

    List<String> getJenkins() {
        ontrack.get('extension/jenkins/configurations/descriptors').resources.collect { it.id }
    }
}
