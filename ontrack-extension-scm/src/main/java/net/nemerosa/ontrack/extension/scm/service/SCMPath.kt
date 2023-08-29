package net.nemerosa.ontrack.extension.scm.service

/**
 * Mixin of a [SCM] interface and a [file path][path].
 */
data class SCMPath(
    val scm: SCM,
    val path: String,
)
