package net.nemerosa.ontrack.git.model.plot

data class GDim(
        val w: Int,
        val h: Int
) {
    companion object {
        fun of(w: Int, h: Int): GDim {
            return GDim(w, h)
        }
    }
}
