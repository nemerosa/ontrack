package net.nemerosa.ontrack.service.job;

import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.nemerosa.ontrack.model.job.*;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.support.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class JobServiceImpl implements ScheduledService,
        JobService,
        ApplicationInfoProvider,
        StartupService,
        JobConsumer {

    private final Logger logger = LoggerFactory.getLogger(JobService.class);
    private final Collection<JobProvider> jobProviders;
    private final SecurityService securityService;
    private final ApplicationLogService applicationLogService;
    private final JobQueueAccessService jobQueueAccessService;

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
    public JobServiceImpl(ApplicationContext applicationContext, SecurityService securityService, ApplicationLogService applicationLogService, JobQueueAccessService jobQueueAccessService) {
        this.jobQueueAccessService = jobQueueAccessService;
        this.jobProviders = applicationContext.getBeansOfType(JobProvider.class).values();
        this.securityService = securityService;
        this.applicationLogService = applicationLogService;
    }

    @Override
    public String getName() {
        return "JobService";
    }

    @Override
    public int startupOrder() {
        return 100;
    }

    @Override
    public void start() {
        jobQueueAccessService.registerQueueListener(this);
    }

    @Override
    public void accept(Job job) {
        runJob(registerJob(-1L, job));
    }

    @Override
    public Collection<JobDescriptor> getRunningJobs() {
        return registeredJobs.values().stream()
                .filter(RegisteredJob::isRunning)
                .map(RegisteredJob::getJobDescriptor)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<JobDescriptor> getRegisteredJobs() {
        return registeredJobs.values().stream()
                .map(RegisteredJob::getJobDescriptor)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApplicationInfo> getApplicationInfoList() {
        return registeredJobs.values().stream()
                .filter(RegisteredJob::isRunning)
                .map(RegisteredJob::getApplicationInfo)
                .collect(Collectors.toList());
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
        if (jobProviders.isEmpty()) {
            return;
        }
        long count = syncCount.incrementAndGet();
        logger.debug("[job] Sync jobs: {}", count);
        // Gets the list of all jobs
        Collection<Job> jobs = jobProviders.stream()
                .flatMap(provider -> provider.getJobs().stream())
                .collect(Collectors.toList());
        // For all jobs
        for (Job job : jobs) {
            registerJob(count, job);
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

    private RegisteredJob registerJob(long count, Job job) {
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
        // OK
        return registeredJob;
    }

    protected void runJobs() {
        logger.debug("[job] Running jobs");
        for (RegisteredJob registeredJob : registeredJobs.values()) {
            runJob(registeredJob);
        }
    }

    private void runJob(RegisteredJob registeredJob) {
        if (registeredJob.isRunning()) {
            logger.debug("[job] Still running: {}", registeredJob);
        } else if (registeredJob.mustStart()) {
            logger.debug("[job] Starting: {}", registeredJob);
            start(registeredJob);
        } else {
            logger.debug("[job] Idle: {}", registeredJob);
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
