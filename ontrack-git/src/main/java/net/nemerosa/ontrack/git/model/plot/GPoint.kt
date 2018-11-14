package net.nemerosa.ontrack.git.model.plot

data class GPoint(
        val x: Int,
        val y: Int
) : AbstractGItem() {

    override val maxX: Int = x
    override val maxY: Int = y

    fun ty(offset: Int): GPoint {
        return of(maxX, maxY + offset)
    }

    companion object {
        fun of(x: Int, y: Int): GPoint {
            return GPoint(x, y)
        }
    }
}
