package net.nemerosa.ontrack.service.support;

import net.nemerosa.ontrack.common.Time;
import net.nemerosa.ontrack.model.security.Account;
import net.nemerosa.ontrack.model.security.ApplicationManagement;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.support.*;
import net.nemerosa.ontrack.repository.ApplicationLogEntriesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ApplicationLogServiceImpl implements ApplicationLogService {

    private final Logger logger = LoggerFactory.getLogger(ApplicationLogService.class);

    private final SecurityService securityService;
    @Deprecated
    private final int maxEntries;
    @Deprecated
    private final LinkedList<ApplicationLogEntry> entries;
    private final ApplicationLogEntriesRepository entriesRepository;
    private final CounterService counterService;

    @Autowired
    public ApplicationLogServiceImpl(OntrackConfigProperties ontrackConfigProperties, SecurityService securityService, ApplicationLogEntriesRepository entriesRepository, CounterService counterService) {
        this.securityService = securityService;
        this.entriesRepository = entriesRepository;
        this.counterService = counterService;
        this.maxEntries = ontrackConfigProperties.getApplicationLogMaxEntries();
        this.entries = new LinkedList<>();
    }

    @Override
    public void log(ApplicationLogEntry entry) {
        ApplicationLogEntry signedEntry = entry.withAuthentication(
                securityService.getAccount().map(Account::getName).orElse("anonymous")
        );
        doLog(signedEntry);
    }

    private synchronized void doLog(ApplicationLogEntry entry) {
        // Logging
        logger.error(
                String.format(
                        "[%s] name=%s,authentication=%s,timestamp=%s,%s%nStacktrace: %s",
                        entry.getLevel(),
                        entry.getType().getName(),
                        entry.getAuthentication(),
                        Time.forStorage(entry.getTimestamp()),
                        entry.getDetailList().stream()
                                .map(nd -> String.format("%s=%s", nd.getName(), nd.getDescription()))
                                .collect(Collectors.joining(",")),
                        entry.getStacktrace()
                )
        );
        // Storage
        entries.addFirst(entry);
        // Pruning
        while (entries.size() > maxEntries) {
            entries.removeLast();
        }
        // Storing in database
        entriesRepository.log(entry);
        // Metrics
        counterService.increment("error");
        counterService.increment(String.format("error.%s", entry.getType().getName()));
    }

    @Override
    public synchronized int getLogEntriesTotal() {
        return entriesRepository.getTotalCount();
    }

    @Override
    public synchronized List<ApplicationLogEntry> getLogEntries(ApplicationLogEntryFilter filter, Page page) {
        securityService.checkGlobalFunction(ApplicationManagement.class);
        return entriesRepository.getLogEntries(filter, page);
    }
}
