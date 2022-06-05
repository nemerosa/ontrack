package net.nemerosa.ontrack.extension.av.processing

import net.nemerosa.ontrack.extension.av.dispatcher.AutoVersioningOrder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AutoVersioningProcessingServiceImpl : AutoVersioningProcessingService {

    override fun process(order: AutoVersioningOrder) {
        TODO("Not yet implemented")
    }

}