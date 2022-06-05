package net.nemerosa.ontrack.extension.av.queue

import net.nemerosa.ontrack.extension.av.dispatcher.AutoVersioningOrder

interface AutoVersioningQueue {

    fun queue(order: AutoVersioningOrder)

}