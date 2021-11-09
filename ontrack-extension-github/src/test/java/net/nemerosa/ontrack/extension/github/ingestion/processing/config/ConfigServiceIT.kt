package net.nemerosa.ontrack.extension.github.ingestion.processing.config

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.common.RunProfile
import net.nemerosa.ontrack.extension.github.ingestion.AbstractIngestionTestSupport
import net.nemerosa.ontrack.extension.github.ingestion.IngestionHookFixtures
import org.junit.Ignore
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@Ignore
class ConfigServiceIT : AbstractIngestionTestSupport() {

//    @Autowired
//    private lateinit var configLoaderService: ConfigLoaderService
//
//    @Autowired
//    private lateinit var configService: ConfigService
//
//    @Test
//    fun `Saving the configuration`() {
//        val config = IngestionHookFixtures.sampleIngestionConfig()
//        every {
//            configLoaderService.loadConfig(
//                any(),
//                INGESTION_CONFIG_FILE_PATH
//            )
//        } returns config
//        val repository = IngestionHookFixtures.sampleRepository()
//        val branch = IngestionHookFixtures.sampleBranch
//        configService.saveConfig(
//            repository,
//            branch,
//            INGESTION_CONFIG_FILE_PATH,
//        )
//        // Loading the configuration
//        assertNotNull(configService.findConfig(repository, branch), "Configuration saved") {
//            assertEquals(config, it)
//        }
//    }
//
//    @Configuration
//    @Profile(RunProfile.UNIT_TEST)
//    class ConfigServiceITConfig {
//
//        private val configLoaderService: ConfigLoaderService = mockk()
//
//        @Bean
//        @Primary
//        fun configLoaderService(): ConfigLoaderService = configLoaderService
//
//    }

}