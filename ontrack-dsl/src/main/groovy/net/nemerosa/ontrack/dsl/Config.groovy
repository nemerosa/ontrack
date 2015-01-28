package net.nemerosa.ontrack.dsl

class Config {

    private final Ontrack ontrack

    Config(Ontrack ontrack) {
        this.ontrack = ontrack
    }

    def gitHub(Map<String, ?> parameters, String name) {
        def params = parameters + [name: name]
        ontrack.post(
                'extension/github/configurations/create',
                params
        )
    }

    def svn(Map<String, ?> parameters, String name) {
        def params = parameters + [name: name]
        ontrack.post(
                'extension/svn/configurations/create',
                params
        )
    }

    def getSvn() {
        ontrack.get('extension/svn/configurations/descriptors').resources.collect { it.name }
    }

    def jenkins(String name, String url, String user = '', String password = '') {
        ontrack.post(
                'extension/jenkins/configurations/create', [
                name    : name,
                url     : url,
                user    : user,
                password: password
        ])
    }
}
