package net.nemerosa.ontrack.extension.av.processing

import net.nemerosa.ontrack.extension.av.dispatcher.AutoVersioningOrder

interface AutoVersioningProcessingService {

    fun process(order: AutoVersioningOrder): AutoVersioningProcessingOutcome

}