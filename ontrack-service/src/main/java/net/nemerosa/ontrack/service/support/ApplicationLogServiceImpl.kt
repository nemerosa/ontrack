package net.nemerosa.ontrack.service.support

import io.micrometer.core.instrument.MeterRegistry
import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.model.security.ApplicationManagement
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.ApplicationLogEntry
import net.nemerosa.ontrack.model.support.ApplicationLogEntryFilter
import net.nemerosa.ontrack.model.support.ApplicationLogService
import net.nemerosa.ontrack.model.support.Page
import net.nemerosa.ontrack.repository.ApplicationLogEntriesRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ApplicationLogServiceImpl(
        private val securityService: SecurityService,
        private val entriesRepository: ApplicationLogEntriesRepository,
        private val meterRegistry: MeterRegistry
) : ApplicationLogService {

    private val logger = LoggerFactory.getLogger(ApplicationLogService::class.java)

    @Transactional(propagation = Propagation.NESTED)
    override fun log(entry: ApplicationLogEntry) {
        val signedEntry = entry.withAuthentication(
                securityService.account.map { it.name }.orElse("anonymous")
        )
        doLog(signedEntry)
    }

    override fun cleanup(retentionDays: Int) {
        entriesRepository.cleanup(retentionDays)
    }

    override fun deleteLogEntries() {
        entriesRepository.deleteLogEntries()
    }

    @Synchronized
    private fun doLog(entry: ApplicationLogEntry) {
        // Logging
        logger.error(
                String.format(
                        "[%s] name=%s,authentication=%s,timestamp=%s,%s%nStacktrace: %s",
                        entry.level,
                        entry.type.name,
                        entry.authentication,
                        Time.forStorage(entry.timestamp),
                        entry.detailList.joinToString(",") { nd -> String.format("%s=%s", nd.name, nd.description) },
                        entry.stacktrace
                )
        )
        // Storing in database
        entriesRepository.log(entry)
        // Metrics
        meterRegistry.counter(
                "ontrack_error",
                "type", entry.type.name
        ).increment()
    }

    @Synchronized
    override fun getLogEntriesTotal(): Int {
        return entriesRepository.totalCount
    }

    @Synchronized
    override fun getLogEntries(filter: ApplicationLogEntryFilter, page: Page): List<ApplicationLogEntry> {
        securityService.checkGlobalFunction(ApplicationManagement::class.java)
        return entriesRepository.getLogEntries(filter, page)
    }
}
