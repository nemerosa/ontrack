package net.nemerosa.ontrack.boot.migration.v5

data class MigrationV5UsersStatus(
    val finalUsers: List<MigrationV5User>,
    val conflicts: Map<String, List<MigrationV5User>>,
)
