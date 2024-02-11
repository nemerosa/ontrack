package net.nemerosa.ontrack.extension.issues.export

/**
 * List of issues, exported as text for a given format.
 */
@Deprecated("Will be removed in V5. Use the templating service")
class ExportedIssues(
        val format: String,
        val content: String
)
