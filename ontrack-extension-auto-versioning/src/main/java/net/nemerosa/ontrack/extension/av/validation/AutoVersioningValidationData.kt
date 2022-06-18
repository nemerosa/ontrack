package net.nemerosa.ontrack.extension.av.validation

data class AutoVersioningValidationData(
        val project: String,
        val version: String,
        val latestVersion: String,
        val path: String,
        val time: Long,
)