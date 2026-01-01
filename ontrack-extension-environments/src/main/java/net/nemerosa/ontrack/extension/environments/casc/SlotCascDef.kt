package net.nemerosa.ontrack.extension.environments.casc

import net.nemerosa.ontrack.model.annotations.APIDescription

interface SlotCascDef {

    @APIDescription("Optional qualifier for this slot")
    val qualifier: String

    @APIDescription("Prefix for the description for the slots (can be overridden by description at environment level)")
    val description: String

    @APIDescription("Configuration of environments for this slot")
    val environments: List<SlotEnvironmentCasc>

}