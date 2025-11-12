package net.nemerosa.ontrack.model.structure

fun autoCompleteSearchIndexSettings() = SearchIndexSettings(
    analyzers = mapOf(
        "autocomplete" to AnalyzerConfig(
            tokenizer = "autocomplete_tokenizer",
            filters = listOf("lowercase")
        ),
        "autocomplete_search" to AnalyzerConfig(
            tokenizer = "standard",
            filters = listOf("lowercase")
        )
    ),
    tokenizers = mapOf(
        "autocomplete_tokenizer" to TokenizerConfig(
            type = "edge_ngram",
            minGram = 3,
            maxGram = 10,
            tokenChars = listOf("letter", "digit", "punctuation")
        )
    )
)
