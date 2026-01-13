package net.nemerosa.ontrack.extension.scm.changelog

data class SemanticType(
    val type: String,
    val title: String,
    val emoji: String?,
)

private val allTypes = listOf(
    SemanticType("build", "Build", "🏗️"),
    SemanticType("chore", "Misc.", "🧹"),
    SemanticType("ci", "CI", "👷"),
    SemanticType("docs", "Documentation", "📝"),
    SemanticType("feat", "Features", "✨"),
    SemanticType("fix", "Fixes", "🐛"),
    SemanticType("style", "Style", "🎨"),
    SemanticType("refactor", "Refactoring", "♻️"),
    SemanticType("perf", "Performance", "⚡"),
    SemanticType("test", "Tests", "✅"),
)

val types = allTypes.associateBy { it.type }
