package net.nemerosa.ontrack.graphql.support

import net.nemerosa.ontrack.model.annotations.APIDescription

class Person(
        @APIDescription("Full name")
        val name: String,
        @APIDescription("Full postal address")
        val address: String,
        val age: Int,
        val isDeveloper: Boolean
)
