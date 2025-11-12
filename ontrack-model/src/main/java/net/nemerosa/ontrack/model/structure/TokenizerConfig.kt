package net.nemerosa.ontrack.model.structure

data class TokenizerConfig(
    val type: String,
    val minGram: Int? = null,
    val maxGram: Int? = null,
    val tokenChars: List<String> = emptyList()
)