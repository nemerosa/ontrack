package net.nemerosa.ontrack.service.support

import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.Page
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.junit.Test

import static org.mockito.Mockito.mock

class ApplicationLogServiceImplTest {

    @Test
    void 'Log entries must be served in reverse order'() {
        OntrackConfigProperties ontrackConfigProperties = new OntrackConfigProperties();
        ontrackConfigProperties.applicationLogMaxEntries = 10
        SecurityService securityService = mock(SecurityService)
        ApplicationLogServiceImpl service = new ApplicationLogServiceImpl(
                ontrackConfigProperties,
                securityService
        )
        // Creates two entries
        service.error(new RuntimeException("Test 1"), ApplicationLogServiceImplTest, "test1", "test1", "test1")
        service.error(new RuntimeException("Test 2"), ApplicationLogServiceImplTest, "test2", "test2", "test2")
        // Gets the entries
        def entries = service.getLogEntries(new Page())
        // Checks the order
        assert entries.size() == 2
        assert entries[0].identifier == "test2"
        assert entries[1].identifier == "test1"
    }

}