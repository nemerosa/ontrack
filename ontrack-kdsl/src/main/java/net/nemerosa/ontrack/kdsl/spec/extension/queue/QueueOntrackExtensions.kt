package net.nemerosa.ontrack.kdsl.spec.extension.queue

import net.nemerosa.ontrack.kdsl.spec.Ontrack


/**
 * Management of queuing in Ontrack.
 */
val Ontrack.queue: QueueMgt get() = QueueMgt(connector)
