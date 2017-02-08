package net.nemerosa.ontrack.service.support;

import net.nemerosa.ontrack.model.security.ApplicationManagement;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.support.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Gets the list of fatal errors.
 */
@Component
public class ApplicationLogFatalInfoProvider implements ApplicationInfoProvider {

    private final OntrackConfigProperties configProperties;
    private final ApplicationLogService logService;
    private final SecurityService securityService;

    @Autowired
    public ApplicationLogFatalInfoProvider(OntrackConfigProperties configProperties, ApplicationLogService logService, SecurityService securityService) {
        this.configProperties = configProperties;
        this.logService = logService;
        this.securityService = securityService;
    }

    @Override
    public List<ApplicationInfo> getApplicationInfoList() {
        if (securityService.isGlobalFunctionGranted(ApplicationManagement.class)) {
            return logService.getLogEntries(
                    new ApplicationLogEntryFilter().withLevel(ApplicationLogEntryLevel.FATAL),
                    new Page(0, configProperties.getApplicationLogInfoMax())
            )
                    .stream()
                    .map(lge -> ApplicationInfo.error(
                            lge.getType().getDescription()
                    ))
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }
}
