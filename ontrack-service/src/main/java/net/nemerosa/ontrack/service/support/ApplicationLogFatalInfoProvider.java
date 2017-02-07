package net.nemerosa.ontrack.service.support;

import net.nemerosa.ontrack.model.support.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Gets the list of fatal errors.
 */
@Component
public class ApplicationLogFatalInfoProvider implements ApplicationInfoProvider {

    private final OntrackConfigProperties configProperties;
    private final ApplicationLogService logService;

    @Autowired
    public ApplicationLogFatalInfoProvider(OntrackConfigProperties configProperties, ApplicationLogService logService) {
        this.configProperties = configProperties;
        this.logService = logService;
    }

    @Override
    public List<ApplicationInfo> getApplicationInfoList() {
        return logService.getLogEntries(
                new ApplicationLogEntryFilter().withLevel(ApplicationLogEntryLevel.FATAL),
                new Page(0, configProperties.getApplicationLogInfoMax())
        )
                .stream()
                .map(lge -> ApplicationInfo.error(
                        lge.getType().getDescription()
                ))
                .collect(Collectors.toList());
    }
}
