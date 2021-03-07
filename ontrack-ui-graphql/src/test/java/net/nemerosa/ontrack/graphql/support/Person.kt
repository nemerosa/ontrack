package net.nemerosa.ontrack.graphql.support

import com.fasterxml.jackson.annotation.JsonProperty
import net.nemerosa.ontrack.model.annotations.APIDescription

class Person(
        @APIDescription("Full name")
        val name: String,
        @APIDescription("Full postal address")
        val address: String,
        val age: Int,
        @get:JsonProperty("developer")
        val isDeveloper: Boolean,
        val experience: Int?
)
