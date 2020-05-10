package net.nemerosa.ontrack.extension.indicators.model.scale

enum class AFScale : Scale<AFScale> {

    A, B, C, D, E, F;

    companion object : ScaleFactory<AFScale> {

        override val name: String = "AF"
        override val description: String = "A to F"

        override fun toScale(value: Int): AFScale =
                when {
                    value <= 20 -> F
                    value in 20..39 -> E
                    value in 40..59 -> D
                    value in 60..79 -> C
                    value in 80..99 -> B
                    else -> A
                }
    }

}