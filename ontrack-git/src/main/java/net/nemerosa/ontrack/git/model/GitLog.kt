package net.nemerosa.ontrack.git.model

import net.nemerosa.ontrack.git.model.plot.GPlot

class GitLog(
        val plot: GPlot,
        val commits: List<GitCommit>
)
