package net.nemerosa.ontrack.model.security

import net.nemerosa.ontrack.common.api.APIDescription

@APIDescription("Granted global permission")
class GlobalPermission(
    @APIDescription("Target the permission is assigned to")
    val target: PermissionTarget,
    @APIDescription("Granted global role")
    val role: GlobalRole,
)
