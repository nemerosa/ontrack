package net.nemerosa.ontrack.service.security;

import net.nemerosa.ontrack.model.annotations.GlobalGrant;
import net.nemerosa.ontrack.model.annotations.ProjectCreation;
import net.nemerosa.ontrack.model.security.SecurityService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class SecurityAspectTest {

    private SecurityAspect aspect;
    private SecurityService securityService;
    private ProceedingJoinPoint pjp;

    @Before
    public void before() {
        securityService = mock(SecurityService.class);
        pjp = mock(ProceedingJoinPoint.class);
        aspect = new SecurityAspect(securityService);
    }

    @Test
    public void global_grant() throws Throwable {
        SecuredClass cls = new SecuredClass();
        Method callMethod = cls.getClass().getMethod("global_grant");

        // Annotation
        GlobalGrant grant = AnnotationUtils.findAnnotation(callMethod, GlobalGrant.class);
        assertNotNull("Grant annotation has not been found", grant);

        // Call
        aspect.doControl(pjp, grant);

        // Checks the security service has been called
        verify(securityService, times(1)).checkGlobalFunction(ProjectCreation.class);
    }

}