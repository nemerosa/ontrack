package net.nemerosa.ontrack.dsl.client

import net.nemerosa.ontrack.dsl.Ontrack

class ConfigResource {

    private final Ontrack ontrack

    ConfigResource(Ontrack ontrack) {
        this.ontrack = ontrack
    }

    def gitHub(Map<String, ?> parameters, String name) {
        def params = parameters + [name: name]
        ontrack.post(
                'extension/github/configurations/create',
                params
        )
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
