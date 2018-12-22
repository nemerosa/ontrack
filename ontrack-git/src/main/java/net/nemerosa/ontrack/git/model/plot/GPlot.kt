package net.nemerosa.ontrack.git.model.plot

import java.util.*

class GPlot {

    private val items = ArrayList<GItem>()

    val width: Int
        get() {
            var width = 0
            for (item in items) {
                width = Math.max(width, item.maxX)
            }
            return width
        }

    val height: Int
        get() {
            var height = 0
            for (item in items) {
                height = Math.max(height, item.maxY)
            }
            return height
        }

    fun add(item: GItem): GPlot {
        items.add(item)
        return this
    }

}
