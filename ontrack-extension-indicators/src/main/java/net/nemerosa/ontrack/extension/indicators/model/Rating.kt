package net.nemerosa.ontrack.extension.indicators.model

enum class Rating {

    F, E, D, C, B, A;

    companion object {

        fun asRating(value: Int): Rating =
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