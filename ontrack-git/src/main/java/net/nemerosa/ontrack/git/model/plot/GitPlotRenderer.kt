package net.nemerosa.ontrack.git.model.plot

import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.revplot.AbstractPlotRenderer
import org.eclipse.jgit.revplot.PlotCommit
import org.eclipse.jgit.revplot.PlotCommitList
import org.eclipse.jgit.revplot.PlotLane
import org.eclipse.jgit.revwalk.RevCommit

class GitPlotRenderer(commitList: PlotCommitList<PlotLane>) : AbstractPlotRenderer<PlotLane, GColor>() {
    val plot: GPlot = GPlot()
    val commits: MutableList<RevCommit> = mutableListOf()
    private val rowHeight = DEFAULT_ROW_HEIGHT
    private var currentCommit: PlotCommit<PlotLane>? = null
    private var rowIndex: Int = 0

    init {
        rowIndex = 0
        for (commit in commitList) {
            commits.add(commit)
            currentCommit = commit
            paintCommit(commit, rowHeight)
            rowIndex++
        }
    }

    override fun drawLabel(x: Int, y: Int, ref: Ref): Int {
        return 0
    }

    override fun laneColor(myLane: PlotLane?): GColor {
        return if (myLane == null) {
            GColor(0)
        } else {
            GColor(myLane.position)
        }
    }

    override fun drawLine(color: GColor, x1: Int, y1: Int, x2: Int, y2: Int, width: Int) {
        plot.add(GLine.of(color, point(x1, y1), point(x2, y2), width))
    }

    override fun drawCommitDot(x: Int, y: Int, w: Int, h: Int) {
        plot.add(
                GOval.of(
                        laneColor(currentCommit!!.lane),
                        point(x, y),
                        GDim.of(w, h)
                )
        )
    }

    override fun drawBoundaryDot(x: Int, y: Int, w: Int, h: Int) {}

    override fun drawText(msg: String, x: Int, y: Int) {}

    private fun point(x1: Int, y1: Int): GPoint {
        return GPoint.of(x1, y1).ty(rowIndex * rowHeight)
    }

    companion object {
        const val DEFAULT_ROW_HEIGHT = 24
    }
}
