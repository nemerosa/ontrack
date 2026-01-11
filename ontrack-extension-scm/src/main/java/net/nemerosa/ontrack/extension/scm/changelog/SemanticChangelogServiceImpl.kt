package net.nemerosa.ontrack.extension.scm.changelog

import org.springframework.stereotype.Component

@Component
class SemanticChangelogServiceImpl : SemanticChangelogService {

    // Regex: type(scope)?: subject
    private val regex = Regex("""^(\w+)(?:\(([^)]+)\))?!?: (.+)$""")

    override fun parseSemanticCommit(message: String): SemanticCommit {
        val match = regex.find(message.lines().first()) ?: return SemanticCommit.subjectOnly(message)

        return SemanticCommit(
            type = match.groupValues[1],
            scope = match.groupValues[2].ifEmpty { null },
            subject = match.groupValues[3],
        )
    }
}