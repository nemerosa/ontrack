package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.job.JobState
import net.nemerosa.ontrack.model.structure.NameDescription

open class JobFilterResources(
        val categories: List<NameDescription>,
        val types: Map<String, List<NameDescription>>
) {
    @Suppress("unused")
    val states: List<NameDescription> = enumValues<JobState>()
            .map {
                NameDescription(
                        it.name,
                        it.description
                )
            }
}