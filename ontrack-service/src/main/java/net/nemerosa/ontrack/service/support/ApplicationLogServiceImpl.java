package net.nemerosa.ontrack.service.support;

import net.nemerosa.ontrack.model.security.ApplicationManagement;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.support.*;
import net.nemerosa.ontrack.service.OntrackConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Service
public class ApplicationLogServiceImpl implements ApplicationLogService {

    private final Logger logger = LoggerFactory.getLogger(ApplicationLogService.class);

    private final SecurityService securityService;
    private final int maxEntries;
    private final LinkedList<ApplicationLogEntry> entries;

    @Autowired
    public ApplicationLogServiceImpl(OntrackConfigProperties ontrackConfigProperties, SecurityService securityService) {
        this.securityService = securityService;
        this.maxEntries = ontrackConfigProperties.getApplicationLogMaxEntries();
        this.entries = new LinkedList<>();
    }

    @Override
    public void error(Throwable exception, Class<?> source, String identifier, String context, String info) {
        logger.error(
                String.format(
                        "[%s/%s] [%s] [%s]",
                        source,
                        identifier,
                        context,
                        info
                ),
                exception
        );
        log(
                new ApplicationLogEntry(
                        ApplicationLogEntryLevel.ERROR,
                        source,
                        identifier,
                        context,
                        info
                ).withException(exception)
        );
    }

    private synchronized void log(ApplicationLogEntry entry) {
        // Storage
        entries.addFirst(entry);
        // Pruning
        while (entries.size() > maxEntries) {
            entries.removeLast();
        }
    }

    @Override
    public synchronized ApplicationLogEntries getLogEntries(Page page) {
        securityService.checkGlobalFunction(ApplicationManagement.class);
        int total = entries.size();
        int offset = page.getOffset();
        int count = page.getCount();
        if (offset >= total) {
            return new ApplicationLogEntries(
                    Collections.emptyList(),
                    new Page(offset, 0),
                    total
            );
        } else {
            List<ApplicationLogEntry> list = new ArrayList<>(entries);
            list = list.subList(offset, Math.min(offset + count, total));
            return new ApplicationLogEntries(
                    list,
                    new Page(offset, list.size()),
                    total
            );
        }
    }
}
