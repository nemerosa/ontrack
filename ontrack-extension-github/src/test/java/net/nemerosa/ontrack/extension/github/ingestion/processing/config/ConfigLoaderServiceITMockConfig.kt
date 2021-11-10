package net.nemerosa.ontrack.extension.github.ingestion.processing.config

import io.mockk.every
import io.mockk.mockk
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

class ConfigLoaderServiceITMockConfig {

    private val configLoaderService: ConfigLoaderService = mockk()

    @Bean
    @Primary
    fun configLoaderService(): ConfigLoaderService = configLoaderService

    companion object {
        private val defaultIngestionConfig = IngestionConfig()
        fun defaultIngestionConfig(configLoaderService: ConfigLoaderService) {
            every {
                configLoaderService.loadConfig(
                    any(),
                    INGESTION_CONFIG_FILE_PATH
                )
            } returns defaultIngestionConfig
        }
    }

}