package net.nemerosa.ontrack.extension.scm.changelog

data class SemanticCommit(
    val type: String?,
    val scope: String?,
    val subject: String,
) {
    companion object {
        fun subjectOnly(subject: String) = SemanticCommit(
            type = null,
            scope = null,
            subject = subject
        )
    }
}
