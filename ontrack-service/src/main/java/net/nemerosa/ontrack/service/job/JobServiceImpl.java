package net.nemerosa.ontrack.service.job;

import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.nemerosa.ontrack.model.job.*;
import net.nemerosa.ontrack.model.security.ApplicationManagement;
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
    public boolean accept(Job job) {
        return !idInSameGroupRunning(job.getGroup(), job.getId())
                && runJob(registerJob(-1L, job));
    }

    @Override
    public Collection<JobStatus> getJobStatuses() {
        securityService.checkGlobalFunction(ApplicationManagement.class);
        return registeredJobs.values().stream()
                .map(this::getJobStatus)
                .collect(Collectors.toList());
    }

    protected JobStatus getJobStatus(RegisteredJob registeredJob) {
        return new JobStatus(
                registeredJob.getJobDescriptor(),
                registeredJob.isRunning(),
                registeredJob.getApplicationInfo(),
                registeredJob.getRunCount(),
                registeredJob.getLastRunDate(),
                registeredJob.getNextRunDate()
        );
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
            registeredJob = RegisteredJob.of(job, count);
            registeredJobs.put(category, id, registeredJob);
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

    private boolean runJob(RegisteredJob registeredJob) {
        if (idInSameGroupRunning(registeredJob)) {
            logger.debug("[job] Same group running: {}", registeredJob);
            return false;
        } else if (registeredJob.isRunning()) {
            logger.debug("[job] Still running: {}", registeredJob);
            return false;
        } else if (registeredJob.mustStart()) {
            logger.debug("[job] Starting: {}", registeredJob);
            start(registeredJob);
            return true;
        } else {
            logger.debug("[job] Idle: {}", registeredJob);
            return false;
        }
    }

    private boolean idInSameGroupRunning(RegisteredJob registeredJob) {
        return idInSameGroupRunning(registeredJob.getJobGroup(), registeredJob.getJobId());
    }

    private boolean idInSameGroupRunning(String group, String id) {
        return registeredJobs.values().stream()
                .filter(r -> group.equals(r.getJobGroup()) && id.equals(r.getJobId()) && r.isRunning())
                .findAny()
                .isPresent();
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
                        getClass(),
                        String.format(
                                "%s/%s/%s",
                                registeredJob.getJobGroup(),
                                registeredJob.getJobCategory(),
                                registeredJob.getJobId()
                        ),
                        registeredJob.getJobDescription(),
                        Optional.ofNullable(registeredJob.getApplicationInfo()).map(ApplicationInfo::getMessage).orElse(null)
                );
            }
        };
        // Submitting the task
        executor.submit(wrappedTask);
    }
}
