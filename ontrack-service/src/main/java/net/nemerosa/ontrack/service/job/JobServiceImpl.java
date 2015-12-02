package net.nemerosa.ontrack.service.job;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.nemerosa.ontrack.model.Ack;
import net.nemerosa.ontrack.model.job.*;
import net.nemerosa.ontrack.model.metrics.OntrackMetrics;
import net.nemerosa.ontrack.model.security.ApplicationManagement;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.support.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.Metric;
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
        OntrackMetrics,
        JobService,
        ApplicationInfoProvider,
        StartupService,
        JobConsumer {

    private final Logger logger = LoggerFactory.getLogger(JobService.class);
    private final Collection<JobProvider> jobProviders;
    private final SecurityService securityService;
    private final ApplicationLogService applicationLogService;
    private final JobQueueAccessService jobQueueAccessService;
    private final CounterService counterService;
    private final MetricRegistry metricRegistry;

    private final AtomicLong syncCount = new AtomicLong();

    /**
     * Category x ID -> Job
     */
    private final Table<String, String, RegisteredJob> registeredJobs = Tables.newCustomTable(
            new HashMap<>(),
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
    public JobServiceImpl(ApplicationContext applicationContext, SecurityService securityService, ApplicationLogService applicationLogService, JobQueueAccessService jobQueueAccessService, CounterService counterService, MetricRegistry metricRegistry) {
        this.jobQueueAccessService = jobQueueAccessService;
        this.counterService = counterService;
        this.metricRegistry = metricRegistry;
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
                && runJob(registerJob(-1L, job), true);
    }

    @Override
    public Collection<JobStatus> getJobStatuses() {
        securityService.checkGlobalFunction(ApplicationManagement.class);
        return registeredJobs.values().stream()
                .map(this::getJobStatus)
                .collect(Collectors.toList());
    }

    @Override
    public Ack launchJob(long id) {
        // Checks rights
        securityService.checkGlobalFunction(ApplicationManagement.class);
        // Gets a registered job
        return registeredJobs.values().stream()
                .filter(j -> j.getId() == id)
                .findFirst()
                .map(j -> Ack.validate(runJob(j, true)))
                .orElseThrow(() -> new JobNotFoundException(id));
    }

    protected JobStatus getJobStatus(RegisteredJob registeredJob) {
        return new JobStatus(
                registeredJob.getId(),
                registeredJob.getJobDescriptor(),
                registeredJob.isRunning(),
                registeredJob.getApplicationInfo(),
                registeredJob.getRunCount(),
                registeredJob.getLastRunDate(),
                registeredJob.getLastRunDurationMs(),
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

    protected RegisteredJob registerJob(long count, Job job) {
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
            runJob(registeredJob, false);
        }
    }

    protected boolean runJob(RegisteredJob registeredJob, boolean forceEarly) {
        if (registeredJob.isDisabled()) {
            logger.debug("[job] Job disabled: {}", registeredJob);
            return false;
        } else if (idInSameGroupRunning(registeredJob)) {
            logger.debug("[job] Same group running: {}", registeredJob);
            return false;
        } else if (registeredJob.isRunning()) {
            logger.debug("[job] Still running: {}", registeredJob);
            return false;
        } else if (forceEarly) {
            logger.debug("[job] Starting forced: {}", registeredJob);
            start(registeredJob);
            return true;
        } else if (registeredJob.mustStart()) {
            logger.debug("[job] Starting: {}", registeredJob);
            start(registeredJob);
            return true;
        } else {
            logger.debug("[job] Idle: {}", registeredJob);
            return false;
        }
    }

    protected boolean idInSameGroupRunning(RegisteredJob registeredJob) {
        return idInSameGroupRunning(registeredJob.getJobGroup(), registeredJob.getJobId());
    }

    protected boolean idInSameGroupRunning(String group, String id) {
        return registeredJobs.values().stream()
                .filter(r -> group.equals(r.getJobGroup()) && id.equals(r.getJobId()) && r.isRunning())
                .findAny()
                .isPresent();
    }

    protected void start(RegisteredJob registeredJob) {
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
        // Metrics wrapper
        Runnable monitoredTask = () -> {
            String jobCategoryMetric = "job-category." + registeredJob.getJobCategory();
            String jobMetric = "job";
            Timer.Context jobTime = metricRegistry.timer(jobMetric).time();
            Timer.Context jobCategoryTime = metricRegistry.timer(jobCategoryMetric).time();
            try {
                counterService.increment(jobMetric);
                counterService.increment(jobCategoryMetric);
                wrappedTask.run();
            } finally {
                counterService.decrement(jobMetric);
                counterService.decrement(jobCategoryMetric);
                jobTime.stop();
                jobCategoryTime.stop();
            }
        };
        // Submitting the task
        executor.submit(monitoredTask);
    }

    @Override
    public Collection<Metric<?>> metrics() {
        List<Metric<?>> metrics = new ArrayList<>();

        // Overall counts
        Collection<RegisteredJob> jobs = registeredJobs.values();
        // Total number of jobs
        metrics.add(new Metric<>("gauge.jobs", jobs.size()));
        // Total number of running jobs
        metrics.add(new Metric<>("gauge.jobs.running", jobs.stream()
                .filter(RegisteredJob::isRunning)
                .count()
        ));
        // Total number of disabled jobs
        metrics.add(new Metric<>("gauge.jobs.disabled", jobs.stream()
                .filter(RegisteredJob::isDisabled)
                .count()
        ));
        // TODO Total number of jobs in error

        // Per categories
        registeredJobs.rowMap().forEach((category, idMap) -> {
            // Total number of jobs per category
            metrics.add(new Metric<>("gauge.jobs." + category, idMap.values().size()));
            // Total number of running jobs per category
            metrics.add(new Metric<>("gauge.jobs." + category + ".running", idMap.values().stream()
                    .filter(RegisteredJob::isRunning)
                    .count()));
            // Total number of disabled jobs per category
            metrics.add(new Metric<>("gauge.jobs." + category + ".disabled", idMap.values().stream()
                    .filter(RegisteredJob::isDisabled)
                    .count()));
            // TODO Total number of jobs in error per category
        });

        // OK
        return metrics;
    }
}
