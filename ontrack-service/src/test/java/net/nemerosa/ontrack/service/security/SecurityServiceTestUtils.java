package net.nemerosa.ontrack.service.security;

import net.nemerosa.ontrack.model.security.SecurityService;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class SecurityServiceTestUtils {

    public static SecurityService securityService() {
        SecurityService service = mock(SecurityService.class);
        when(service.runAsAdmin(any(Runnable.class))).thenAnswer(invocation -> (Runnable) invocation.getArguments()[0]);
        return service;
    }

}
