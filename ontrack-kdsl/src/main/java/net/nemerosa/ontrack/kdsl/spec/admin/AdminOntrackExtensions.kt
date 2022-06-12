package net.nemerosa.ontrack.kdsl.spec.admin

import net.nemerosa.ontrack.kdsl.spec.Ontrack

val Ontrack.admin: AdminMgt get() = AdminMgt(connector)
