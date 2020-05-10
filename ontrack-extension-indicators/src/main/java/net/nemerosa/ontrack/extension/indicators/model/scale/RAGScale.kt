package net.nemerosa.ontrack.extension.indicators.model.scale

enum class RAGScale : Scale<RAGScale> {

    RED,

    AMBER,

    GREEN;

    companion object : ScaleFactory<RAGScale> {

        override val name: String = "RAG"
        override val description: String = "Red / Amber / Green"

        override fun toScale(value: Int): RAGScale =
                when {
                    value <= 25 -> RED
                    value in 26..99 -> AMBER
                    else -> GREEN
                }
    }

}