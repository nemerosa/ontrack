package net.nemerosa.ontrack.model.security;

class PermissionTarget(
        val type: PermissionTargetType,
        val id: Int,
        val name: String,
        val description: String?
)
