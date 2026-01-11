package net.nemerosa.ontrack.extension.scm.changelog

interface SemanticChangelogService {

    fun parseSemanticCommit(message: String): SemanticCommit

}