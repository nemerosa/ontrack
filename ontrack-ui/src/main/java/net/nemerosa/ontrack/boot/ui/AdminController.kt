package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.job.JobScheduler
import net.nemerosa.ontrack.job.JobStatus
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.security.ApplicationManagement
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.support.ApplicationLogEntry
import net.nemerosa.ontrack.model.support.ApplicationLogEntryFilter
import net.nemerosa.ontrack.model.support.ApplicationLogService
import net.nemerosa.ontrack.model.support.Page
import net.nemerosa.ontrack.ui.controller.AbstractResourceController
import net.nemerosa.ontrack.ui.resource.Pagination
import net.nemerosa.ontrack.ui.resource.Resource
import net.nemerosa.ontrack.ui.resource.Resources
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.actuate.endpoint.HealthEndpoint
import org.springframework.boot.actuate.health.Health
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import javax.validation.Valid
import kotlin.reflect.full.memberProperties

@RestController
@RequestMapping("/admin")
class AdminController
@Autowired
constructor(
        private val jobScheduler: JobScheduler,
        private val applicationLogService: ApplicationLogService,
        private val healthEndpoint: HealthEndpoint,
        private val securityService: SecurityService
) : AbstractResourceController() {

    /**
     * Gets the health status
     */
    @GetMapping("status")
    fun getStatus(): Resource<Health> = Resource.of(
            healthEndpoint.invoke(),
            uri(on(javaClass).getStatus())
    )

    /**
     * Gets the list of application log entries
     */
    @GetMapping("logs")
    fun getLogEntries(filter: ApplicationLogEntryFilter, page: Page): Resources<ApplicationLogEntry> {
        // Gets the entries
        val entries = applicationLogService.getLogEntries(
                filter,
                page
        )
        // Builds the resources
        val resources = Resources.of(
                entries,
                uri(on(javaClass).getLogEntries(filter, page))
        )
        // Pagination information
        val offset = page.offset
        val count = page.count
        val actualCount = entries.size
        val total = applicationLogService.logEntriesTotal
        var pagination = Pagination.of(offset, actualCount, total)
        // Previous page
        if (offset > 0) {
            pagination = pagination.withPrev(
                    uri(on(AdminController::class.java).getLogEntries(
                            filter,
                            Page(
                                    Math.max(0, offset - count),
                                    count
                            )
                    ))
            )
        }
        // Next page
        if (offset + count < total) {
            pagination = pagination.withNext(
                    uri(on(AdminController::class.java).getLogEntries(
                            filter,
                            Page(
                                    offset + count,
                                    count
                            )
                    ))
            )
        }
        // OK
        return resources.withPagination(pagination)
    }

    /**
     * Deletes all application log entries
     */
    @DeleteMapping("logs")
    fun deleteLogEntries(): Ack {
        applicationLogService.deleteLogEntries()
        return Ack.OK
    }

    /**
     * Gets the job filters
     */
    @GetMapping("jobs/filter")
    fun getJobFilter(): JobFilterResources {
        // All job types
        val types = jobScheduler.allJobKeys
                .map { it.type }
                .distinctBy { it.key }
        // All categories
        val categories = types
                .map { it.category }
                .distinctBy { it.key }
                .map {
                    NameDescription(
                            it.key,
                            it.name
                    )
                }
        // Indexation of types per category
        val indexedTypes = types.groupBy { it.category.key }
        // OK
        return JobFilterResources(
                categories,
                indexedTypes.mapValues { (_, typeList) ->
                    typeList.map { NameDescription(it.key, it.name) }
                }
        )
    }

    /**
     * Gets the list of jobs and their status
     */
    @GetMapping("jobs")
    fun getJobs(
            @Valid jobFilter: JobFilter?,
            page: Page?
    ): Resources<JobStatus> {
        securityService.checkGlobalFunction(ApplicationManagement::class.java)
        val jobs = (jobFilter ?: JobFilter()).filter(jobScheduler.jobStatuses)
        val pagination = Pagination.paginate(
                jobs,
                page ?: Page(),
                { offset, limit ->
                    uri(on(javaClass).getJobs(null, null)).map(jobFilter, Page(offset, limit))
                }
        )
        return Resources.of(
                pagination.items,
                uri(on(javaClass).getJobs(jobFilter, page))
        )
                .withPagination(pagination.pagination)
                .with(
                        "_pause",
                        uri(on(javaClass).pauseAllJobs()),
                        !jobScheduler.isPaused
                )
                .with(
                        "_resume",
                        uri(on(javaClass).resumeAllJobs()),
                        jobScheduler.isPaused
                )
    }

    /**
     * Launches a job
     */
    @PostMapping("jobs/{id:\\d+}")
    fun launchJob(@PathVariable id: Long): Ack {
        securityService.checkGlobalFunction(ApplicationManagement::class.java)
        return jobScheduler.getJobKey(id)
                .map { key -> Ack.validate(jobScheduler.fireImmediately(key) != null) }
                .orElse(Ack.NOK)
    }

    /**
     * Pauses all job executions
     */
    @PutMapping("jobs/pause")
    fun pauseAllJobs(): Ack {
        securityService.checkGlobalFunction(ApplicationManagement::class.java)
        jobScheduler.pause()
        return Ack.OK
    }

    /**
     * Resumes all job executions
     */
    @PutMapping("jobs/resume")
    fun resumeAllJobs(): Ack {
        securityService.checkGlobalFunction(ApplicationManagement::class.java)
        jobScheduler.resume()
        return Ack.OK
    }

    /**
     * Pauses a job
     */
    @PostMapping("jobs/{id:\\d+}/pause")
    fun pauseJob(@PathVariable id: Long): Ack {
        securityService.checkGlobalFunction(ApplicationManagement::class.java)
        return jobScheduler.getJobKey(id)
                .map { key -> Ack.validate(jobScheduler.pause(key)) }
                .orElse(Ack.NOK)
    }

    /**
     * Resumes a job
     */
    @PostMapping("jobs/{id:\\d+}/resume")
    fun resumeJob(@PathVariable id: Long): Ack {
        securityService.checkGlobalFunction(ApplicationManagement::class.java)
        return jobScheduler.getJobKey(id)
                .map { key -> Ack.validate(jobScheduler.resume(key)) }
                .orElse(Ack.NOK)
    }

    /**
     * Deleting a job
     */
    @DeleteMapping("jobs/{id:\\d+}")
    fun deleteJob(@PathVariable id: Long): Ack {
        securityService.checkGlobalFunction(ApplicationManagement::class.java)
        return jobScheduler.getJobKey(id)
                .filter { key -> !jobScheduler.getJobStatus(key).get().isValid }
                .map { key -> Ack.validate(jobScheduler.unschedule(key)) }
                .orElse(Ack.NOK)
    }

    /**
     * Stopping a job
     */
    @DeleteMapping("jobs/{id:\\d+}/stop")
    fun stopJob(@PathVariable id: Long): Ack {
        securityService.checkGlobalFunction(ApplicationManagement::class.java)
        return jobScheduler.getJobKey(id)
                .map { key -> Ack.validate(jobScheduler.stop(key)) }
                .orElse(Ack.NOK)
    }

}
