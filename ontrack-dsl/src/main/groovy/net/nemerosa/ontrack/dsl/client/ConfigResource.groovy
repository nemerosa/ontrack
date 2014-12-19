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
}
