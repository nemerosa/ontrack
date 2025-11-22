package net.nemerosa.ontrack.boot.migration.v5

data class MigrationV5User(
    val id: Int,
    val email: String,
    val fullName: String,
    val groups: List<String>,
)
