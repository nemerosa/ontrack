package net.nemerosa.ontrack.extension.av.validation

import net.nemerosa.ontrack.model.structure.Build

data class VersionInfo(
    val current: BuildVersionInfo?,
    val last: BuildVersionInfo?,
)

data class BuildVersionInfo(
    val build: Build,
    val version: String,
)
