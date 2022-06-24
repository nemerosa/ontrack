package net.nemerosa.ontrack.extension.dm.tse

import net.nemerosa.ontrack.model.structure.Project

interface TimeSinceEventService {

    fun collectTimesSinceEvents(project: Project, logger: (String) -> Unit)

}