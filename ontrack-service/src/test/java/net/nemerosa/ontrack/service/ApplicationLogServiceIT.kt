package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.common.BaseException
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.support.ApplicationLogEntry
import net.nemerosa.ontrack.model.support.ApplicationLogEntryFilter
import net.nemerosa.ontrack.model.support.ApplicationLogService
import net.nemerosa.ontrack.model.support.Page
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.annotation.DirtiesContext
import org.springframework.transaction.annotation.Transactional
import java.util.*
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

@DirtiesContext
class ApplicationLogServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var sampleService: SampleService

    @Autowired
    private lateinit var applicationLogService: ApplicationLogService

    @Test
    fun `Errors must be logged even when transaction is rolled back`() {
        // ID of the test
        val uuid = UUID.randomUUID().toString()
        // We expect a failure
        assertFailsWith<SampleServiceException> {
            sampleService.run(uuid)
        }
        // ... but the error must be logged
        val filter = ApplicationLogEntryFilter().withText(uuid)
        val entries = asAdmin {
            applicationLogService.getLogEntries(filter, Page(count = 1))
        }
        assertTrue(entries.isNotEmpty(), "Log entry was found")
    }

    interface SampleService {
        fun run(uuid: String)
    }

    class SampleServiceInitialException : BaseException("Sample message")
    class SampleServiceException : BaseException("Sample message")

    open class SampleServiceImpl(
            private val applicationLogService: ApplicationLogService
    ) : SampleService {
        @Transactional
        override fun run(uuid: String) {
            applicationLogService.log(
                    ApplicationLogEntry.error(
                            SampleServiceInitialException(),
                            NameDescription.nd("sample", "Sample error"),
                            "Error $uuid"
                    )
            )
            throw SampleServiceException()
        }
    }

    @Configuration
    class ApplicationLogServiceITConfiguration(
            private val applicationLogService: ApplicationLogService
    ) {
        @Bean
        fun sampleService(): SampleService = SampleServiceImpl(applicationLogService)
    }

}