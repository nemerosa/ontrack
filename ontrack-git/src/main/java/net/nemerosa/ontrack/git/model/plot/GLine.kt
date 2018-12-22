package net.nemerosa.ontrack.git.model.plot

class GLine(
        val color: GColor,
        val a: GPoint,
        val b: GPoint,
        val width: Int
) : AbstractGItem() {


    override val maxX: Int
        get() = Math.max(a.maxX, b.maxX)

    override val maxY: Int
        get() = Math.max(a.maxY, b.maxY)

    companion object {
        fun of(color: GColor, a: GPoint, b: GPoint, width: Int): GLine {
            return GLine(color, a, b, width)
        }
    }
}
