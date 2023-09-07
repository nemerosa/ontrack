package net.nemerosa.ontrack.extension.av.processing

import net.nemerosa.ontrack.extension.av.dispatcher.AutoVersioningOrder

interface AutoVersioningCompletionListener {

    fun onAutoVersioningCompletion(order: AutoVersioningOrder, outcome: AutoVersioningProcessingOutcome)

}