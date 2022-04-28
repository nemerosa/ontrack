package net.nemerosa.ontrack.extension.github.ingestion.queue

import net.nemerosa.ontrack.extension.github.ingestion.payload.InProcessIngestionHookPayloadStorage
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.StorageService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

class AsyncIngestionHookQueueScalableConfiguration {

    @Bean
    @Primary
    fun internalIngestionHookPayloadStorage(
        storageService: StorageService,
        securityService: SecurityService,
    ) = InProcessIngestionHookPayloadStorage(
        storageService,
        securityService,
    )

}