package net.nemerosa.ontrack.graphql.support

class Group(
    val name: String,
    @ListRef
    val accounts: List<Account>,
)