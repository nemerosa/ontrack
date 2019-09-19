package net.nemerosa.ontrack.git.model.plot

data class GColor(
        val index: Int
) {
    companion object {
        fun of(index: Int): GColor {
            return GColor(index)
        }
    }
}
