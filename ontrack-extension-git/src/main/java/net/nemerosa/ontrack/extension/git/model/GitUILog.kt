package net.nemerosa.ontrack.extension.git.model

import net.nemerosa.ontrack.git.model.plot.GPlot

class GitUILog(
        val plot: GPlot,
        val commits: List<GitUICommit>
)
