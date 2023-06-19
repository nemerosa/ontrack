package net.nemerosa.ontrack.graphql

import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class PingController {

    @QueryMapping
    fun ping(): String = "pong"

    @MutationMapping("ping")
    fun pingMutation(): String = ping()

}