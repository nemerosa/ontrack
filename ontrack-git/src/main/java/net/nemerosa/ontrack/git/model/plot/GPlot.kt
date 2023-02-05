package net.nemerosa.ontrack.git.model.plot

import kotlin.math.max

class GPlot {

    val items = mutableListOf<GItem>()

    val width: Int
        get() {
            var width = 0
            for (item in items) {
                width = max(width, item.maxX)
            }
            return width
        }

    val height: Int
        get() {
            var height = 0
            for (item in items) {
                height = max(height, item.maxY)
            }
            return height
        }

    fun add(item: GItem): GPlot {
        items.add(item)
        return this
    }

}
