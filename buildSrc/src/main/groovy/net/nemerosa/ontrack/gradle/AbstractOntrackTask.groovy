package net.nemerosa.ontrack.gradle

import net.nemerosa.ontrack.dsl.Ontrack
import net.nemerosa.ontrack.dsl.OntrackConnection
import org.gradle.api.DefaultTask

abstract class AbstractOntrackTask extends DefaultTask {

    String ontrackUrl = project.properties.ontrackUrl
    String ontrackUser = project.properties.ontrackUser
    String ontrackPassword = project.properties.ontrackPassword

    protected Ontrack getOntrackClient(boolean logging = true) {
        def builder = OntrackConnection.create(ontrackUrl)
                .authenticate(ontrackUser, ontrackPassword)
        if (logging) {
            builder = builder.logger({ println "[${name}][Ontrack] ${it}" })
        }
        return builder.build()
    }
}
