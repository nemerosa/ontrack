package net.nemerosa.ontrack.git.model.plot

data class GOval(
        val color: GColor,
        val c: GPoint,
        val d: GDim
) : AbstractGItem() {

    override val maxX: Int
        get() = c.x + d.w

    override val maxY: Int
        get() = c.y + d.h

    companion object {
        fun of(color: GColor, c: GPoint, d: GDim): GOval {
            return GOval(color, c, d)
        }
    }
}
