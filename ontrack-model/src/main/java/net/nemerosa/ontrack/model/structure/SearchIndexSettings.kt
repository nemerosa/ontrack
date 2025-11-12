package net.nemerosa.ontrack.model.structure

data class SearchIndexSettings(
    val analyzers: Map<String, AnalyzerConfig> = emptyMap(),
    val tokenizers: Map<String, TokenizerConfig> = emptyMap()
)