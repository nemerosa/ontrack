package net.nemerosa.ontrack.service.support

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.security.ApplicationManagement
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.support.ApplicationLogEntry
import net.nemerosa.ontrack.model.support.ApplicationLogEntryFilter.none
import net.nemerosa.ontrack.model.support.ApplicationLogService
import net.nemerosa.ontrack.model.support.Page
import net.nemerosa.ontrack.test.TestUtils
import org.apache.commons.lang3.exception.ExceptionUtils
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class ApplicationLogServiceImplIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var logService: ApplicationLogService

    @Test
    fun `Log entries must be served in reverse order`() {
        // UUID
        val e1 = TestUtils.uid("T")
        val e2 = TestUtils.uid("T")
        // Creates two entries
        logService.log(ApplicationLogEntry.error(
                RuntimeException("Test 1"),
                NameDescription.nd("test", "Test"),
                e1
        ))
        logService.log(ApplicationLogEntry.error(
                RuntimeException("Test 2"),
                NameDescription.nd("test", "Test"),
                e2
        ))
        // Gets the entries
        val entries = asUser().with(ApplicationManagement::class.java).call {
            logService.getLogEntries(none(), Page())
        }
        // Filter the entries
        val filteredEntries = entries.filter { it.information == e1 || it.information == e2 }
        // Checks the order
        assertEquals(2, filteredEntries.size)
        assertEquals("test", filteredEntries[0].type.name)
        assertEquals(e2, filteredEntries[0].information)
        assertEquals("test", filteredEntries[1].type.name)
        assertEquals(e1, filteredEntries[1].information)
    }

    @Test
    fun `Retrieving log entry with details`() {
        // UUID
        val e = TestUtils.uid("T")
        // Creates one entry
        logService.log(ApplicationLogEntry.error(
                RuntimeException("Test 1"),
                NameDescription.nd("test", "Test"),
                e
        ).withDetail("detail1", "value1").withDetail("detail2", "value2"))
        // Gets the entries
        val entries = asUser().with(ApplicationManagement::class.java).call {
            logService.getLogEntries(none(), Page())
        }
        // Filter the entries
        val filteredEntries = entries.filter { it.information == e }
        // Gets the details back
        assertEquals(1, filteredEntries.size)
        assertEquals(
                listOf(
                        NameDescription.nd("detail1", "value1"),
                        NameDescription.nd("detail2", "value2"),
                ),
                filteredEntries[0].detailList
        )
    }

    @Test
    fun `Retrieving log entry with stack trace`() {
        // UUID
        val e = TestUtils.uid("T")
        // Exception
        val exception = RuntimeException("Test 1")
        val stack = ExceptionUtils.getStackTrace(exception)
        // Creates one entry
        logService.log(ApplicationLogEntry.error(
                exception,
                NameDescription.nd("test", "Test"),
                e
        ))
        // Gets the entries
        val entries = asUser().with(ApplicationManagement::class.java).call {
            logService.getLogEntries(none(), Page())
        }
        // Filter the entries
        val filteredEntries = entries.filter { it.information == e }
        // Gets the details back
        assertEquals(1, filteredEntries.size)
        assertEquals(stack, filteredEntries.first().stacktrace)
    }

}