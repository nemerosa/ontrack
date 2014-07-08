package net.nemerosa.ontrack.service.job;

import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.nemerosa.ontrack.model.job.Job;
import net.nemerosa.ontrack.model.job.JobProvider;
import net.nemerosa.ontrack.model.job.JobService;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.support.ApplicationLogService;
import net.nemerosa.ontrack.model.support.ScheduledService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

// TODO Implements JobService
@Service
public class JobServiceImpl implements ScheduledService {

    private final Logger logger = LoggerFactory.getLogger(JobService.class);
    private final Collection<JobProvider> jobProviders;
    private final SecurityService securityService;
    private final ApplicationLogService applicationLogService;

    private final AtomicLong syncCount = new AtomicLong();
    private final Table<String, String, RegisteredJob> registeredJobs = Tables.newCustomTable(
            new HashMap<String, Map<String, RegisteredJob>>(),
            HashMap::new
    );

    /**
     * Runner for the jobs
     */
    private final ExecutorService executor = Executors.newFixedThreadPool(
            10,
            new ThreadFactoryBuilder()
                    .setDaemon(true)
                    .setNameFormat("Job %s")
                    .build()
    );


    @Autowired
    public JobServiceImpl(Collection<JobProvider> jobProviders, SecurityService securityService, ApplicationLogService applicationLogService) {
        this.jobProviders = jobProviders;
        this.securityService = securityService;
        this.applicationLogService = applicationLogService;
    }

    @Override
    public Runnable getTask() {
        return securityService.runAsAdmin(this::syncJobs);
    }

    @Override
    public Trigger getTrigger() {
        return new PeriodicTrigger(1, TimeUnit.MINUTES);
    }

    protected void syncJobs() {
        long count = syncCount.incrementAndGet();
        logger.debug("[job] Sync jobs: {}", count);
        // Gets the list of all jobs
        Collection<Job> jobs = jobProviders.stream()
                .flatMap(provider -> provider.getJobs().stream())
                .collect(Collectors.toList());
        // For all jobs
        for (Job job : jobs) {
            String category = job.getCategory();
            String id = job.getId();
            // Existing job?
            RegisteredJob registeredJob = registeredJobs.get(category, id);
            if (registeredJob != null) {
                // Assuming it is the same
                registeredJob.sync(job, count);
            }
            // New job
            else {
                registeredJobs.put(category, id, RegisteredJob.of(job, count));
            }
        }
        // Checks the obsolescence of jobs in the registered jobs table
        Iterator<RegisteredJob> i = registeredJobs.values().iterator();
        while (i.hasNext()) {
            RegisteredJob registeredJob = i.next();
            if (!registeredJob.checkSync(count)) {
                i.remove();
            }
        }
        // Run the jobs now
        runJobs();
    }

    protected void runJobs() {
        logger.debug("[job] Running jobs");
        for (RegisteredJob registeredJob : registeredJobs.values()) {
            if (registeredJob.isRunning()) {
                logger.debug("[job] Still running: {}", registeredJob);
            } else if (registeredJob.mustStart()) {
                logger.debug("[job] Starting: {}", registeredJob);
                start(registeredJob);
            } else {
                logger.debug("[job] Idle: {}", registeredJob);
            }
        }
    }

    private void start(RegisteredJob registeredJob) {
        // Raw task to execute
        Runnable task = registeredJob.createTask();
        // Running it as admin
        Runnable adminTask = securityService.runAsAdmin(task);
        // Exception handling
        Runnable wrappedTask = () -> {
            try {
                adminTask.run();
            } catch (Exception ex) {
                applicationLogService.error(
                        ex,
                        registeredJob.getJobCategory(),
                        Collections.emptyMap(),
                        registeredJob.getJobDescription()
                );
            }
        };
        // Submitting the task
        executor.submit(wrappedTask);
    }
}
