package net.nemerosa.ontrack.service.support

import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.support.ApplicationLogEntry
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import net.nemerosa.ontrack.model.support.Page
import org.junit.Test
import org.springframework.boot.actuate.metrics.CounterService

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class ApplicationLogServiceImplTest {

    @Test
    void 'Log entries must be served in reverse order'() {
        OntrackConfigProperties ontrackConfigProperties = new OntrackConfigProperties();
        ontrackConfigProperties.applicationLogMaxEntries = 10

        SecurityService securityService = mock(SecurityService)
        when(securityService.getAccount()).thenReturn(Optional.empty())

        CounterService counterService = mock(CounterService)
        ApplicationLogServiceImpl service = new ApplicationLogServiceImpl(
                ontrackConfigProperties,
                securityService,
                counterService
        )
        // Creates two entries
        service.log(ApplicationLogEntry.error(
                new RuntimeException("Test 1"),
                NameDescription.nd("test1", "Test 1")
        ))
        service.log(ApplicationLogEntry.error(
                new RuntimeException("Test 2"),
                NameDescription.nd("test2", "Test 2")
        ))
        // Gets the entries
        def entries = service.getLogEntries(new Page())
        // Checks the order
        assert entries.size() == 2
        assert entries[0].type.name == "test2"
        assert entries[1].type.name == "test1"
    }

}