package net.nemerosa.ontrack.model.structure

data class AnalyzerConfig(
    val type: String = "custom",
    val tokenizer: String,
    val filters: List<String> = emptyList()
)