package net.nemerosa.ontrack.service.support;

import net.nemerosa.ontrack.model.security.ApplicationManagement;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.support.ApplicationLogEntry;
import net.nemerosa.ontrack.model.support.ApplicationLogEntryLevel;
import net.nemerosa.ontrack.model.support.ApplicationLogService;
import net.nemerosa.ontrack.model.support.Page;
import net.nemerosa.ontrack.model.support.OntrackConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
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
    private final CounterService counterService;

    @Autowired
    public ApplicationLogServiceImpl(OntrackConfigProperties ontrackConfigProperties, SecurityService securityService, CounterService counterService) {
        this.securityService = securityService;
        this.counterService = counterService;
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
        counterService.increment("error");
        counterService.increment(String.format("error.%s", source.getSimpleName()));
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
    public synchronized int getLogEntriesTotal() {
        return entries.size();
    }

    @Override
    public synchronized List<ApplicationLogEntry> getLogEntries(Page page) {
        securityService.checkGlobalFunction(ApplicationManagement.class);
        int total = entries.size();
        int offset = page.getOffset();
        int count = page.getCount();
        if (offset >= total) {
            return Collections.emptyList();
        } else {
            List<ApplicationLogEntry> list = new ArrayList<>(entries);
            list = list.subList(offset, Math.min(offset + count, total));
            return list;
        }
    }
}
