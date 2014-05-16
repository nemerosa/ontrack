package net.nemerosa.ontrack.service.security;

import net.nemerosa.ontrack.model.annotations.GlobalGrant;
import net.nemerosa.ontrack.model.security.SecurityService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class SecurityAspect {

    private final SecurityService securityService;

    @Autowired
    public SecurityAspect(SecurityService securityService) {
        this.securityService = securityService;
    }

    @Around("execution(public * net.nemerosa.ontrack..*(..)) && @annotation(grant)")
    public Object doControl(ProceedingJoinPoint pjp, GlobalGrant grant) throws Throwable {
        // Checks the grant
        securityService.checkGlobalFunction(grant.value());
        // Proceeds with the call
        return pjp.proceed();
    }

}