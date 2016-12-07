package net.nemerosa.ontrack.service.support

import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.security.ApplicationManagement
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.support.ApplicationLogEntry
import net.nemerosa.ontrack.model.support.ApplicationLogService
import net.nemerosa.ontrack.model.support.Page
import net.nemerosa.ontrack.test.TestUtils
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class ApplicationLogServiceImplIT extends AbstractServiceTestSupport {

    @Autowired
    private ApplicationLogService logService

    @Test
    void 'Log entries must be served in reverse order'() {
        // UUID
        String e1 = TestUtils.uid("T")
        String e2 = TestUtils.uid("T")
        // Creates two entries
        logService.log(ApplicationLogEntry.error(
                new RuntimeException("Test 1"),
                NameDescription.nd("test", "Test"),
                e1
        ))
        logService.log(ApplicationLogEntry.error(
                new RuntimeException("Test 2"),
                NameDescription.nd("test", "Test"),
                e2
        ))
        // Gets the entries
        def entries = asUser().with(ApplicationManagement).call { logService.getLogEntries(new Page()) }
        // Filter the entries
        def filteredEntries = entries.findAll { it.information == e1 || it.information == e2 }
        // Checks the order
        assert filteredEntries.size() == 2
        assert filteredEntries[0].type.name == "test"
        assert filteredEntries[0].information == e2
        assert filteredEntries[1].type.name == "test"
        assert filteredEntries[1].information == e1
    }

}