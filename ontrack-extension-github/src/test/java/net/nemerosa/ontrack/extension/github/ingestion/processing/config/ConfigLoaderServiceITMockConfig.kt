package net.nemerosa.ontrack.extension.github.ingestion.processing.config

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.extension.github.ingestion.config.model.IngestionConfig
import net.nemerosa.ontrack.extension.github.ingestion.config.model.IngestionConfigSteps
import net.nemerosa.ontrack.extension.github.ingestion.config.model.support.FilterConfig
import net.nemerosa.ontrack.extension.github.ingestion.config.parser.ConfigParsingException
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

class ConfigLoaderServiceITMockConfig {

    private val configLoaderService: ConfigLoaderService = mockk()

    @Bean
    @Primary
    fun configLoaderService(): ConfigLoaderService = configLoaderService

    companion object {
        private val defaultIngestionConfig = IngestionConfig(
            steps = IngestionConfigSteps(
                filter = FilterConfig.all
            )
        )
        fun defaultIngestionConfig(configLoaderService: ConfigLoaderService) {
            customIngestionConfig(configLoaderService, defaultIngestionConfig)
        }

        fun customIngestionConfig(configLoaderService: ConfigLoaderService, config: IngestionConfig) {
            every {
                configLoaderService.loadConfig(
                    configuration = any(),
                    repository = any(),
                    branch = any(),
                    path = INGESTION_CONFIG_FILE_PATH
                )
            } returns config
            every {
                configLoaderService.loadConfig(
                    branch = any(),
                    path = INGESTION_CONFIG_FILE_PATH
                )
            } returns config
        }

        fun failedIngestionConfig(configLoaderService: ConfigLoaderService) {
            every {
                configLoaderService.loadConfig(
                    configuration = any(),
                    repository = any(),
                    branch = any(),
                    path = INGESTION_CONFIG_FILE_PATH
                )
            } throws ConfigParsingException(RuntimeException("Parsing error"))
            every {
                configLoaderService.loadConfig(
                    branch = any(),
                    path = INGESTION_CONFIG_FILE_PATH
                )
            } throws ConfigParsingException(RuntimeException("Parsing error"))
        }
    }

}