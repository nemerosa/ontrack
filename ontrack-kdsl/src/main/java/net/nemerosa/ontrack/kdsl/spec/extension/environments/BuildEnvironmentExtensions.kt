package net.nemerosa.ontrack.kdsl.spec.extension.environments

import net.nemerosa.ontrack.kdsl.spec.Build

fun Build.startPipeline(slot: Slot): SlotPipeline =
    slot.createPipeline(this)
