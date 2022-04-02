package net.nemerosa.ontrack.kdsl.spec.settings

import net.nemerosa.ontrack.kdsl.spec.Ontrack

val Ontrack.settings: SettingsMgt get() = SettingsMgt(connector)
