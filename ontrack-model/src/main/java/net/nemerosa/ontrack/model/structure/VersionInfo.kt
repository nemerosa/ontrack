package net.nemerosa.ontrack.model.structure

import java.time.LocalDateTime

/**
 * Information about the version of the application.
 */
data class VersionInfo(
    /**
     * Creation date
     */
    val date: LocalDateTime,
    /**
     * Display version. Example: 2.3 or master or feature/158-my-feature
     */
    val display: String,
    /**
     * Full version string, including the build number
     */
    val full: String,
    /**
     * Base version string, without the build number
     */
    val branch: String,
    /**
     * Build number
     */
    val build: String,
    /**
     * Associated commit (hash)
     */
    val commit: String,
    /**
     * Source of the version. It can be a tag (correct for a real release) or a developer environment.
     */
    val source: String,
    /**
     * Type of source for the version.
     */
    val sourceType: String
)
