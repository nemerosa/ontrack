package net.nemerosa.ontrack.model.support

import net.nemerosa.ontrack.common.RunProfile

fun EnvService.isProfileEnabled(profile: String): Boolean {
    val profiles = this.profiles.split(',').map { it.trim() }
    return profiles.contains(profile)
}

fun EnvService.isProdProfile() =
    isProfileEnabled(RunProfile.PROD)
