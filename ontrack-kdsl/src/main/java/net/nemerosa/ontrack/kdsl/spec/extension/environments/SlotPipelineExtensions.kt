package net.nemerosa.ontrack.kdsl.spec.extension.environments

import net.nemerosa.ontrack.kdsl.connector.Connected
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.fragment.SlotPipelineFragment
import net.nemerosa.ontrack.kdsl.spec.toBuild

fun SlotPipelineFragment.toPipeline(connected: Connected) = SlotPipeline(
    connector = connected.connector,
    id = id(),
    number = number()!!,
    status = status(),
    slot = slot().fragments().slotFragment().toSlot(connected),
    build = build().fragments().buildFragment().toBuild(connected),
)
