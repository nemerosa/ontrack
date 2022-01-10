package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.it.AbstractServiceTestJUnit4Support
import net.nemerosa.ontrack.job.*
import net.nemerosa.ontrack.model.support.SettingsRepository
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class JobConfigIT extends AbstractServiceTestJUnit4Support {

    public static class TestJob implements Job {

        private final String key;

        TestJob(String key) {
            this.key = key
        }

        @Override
        JobKey getKey() {
            return JobCategory.of("test-category").getType("test-type").getKey(key);
        }

        @Override
        JobRun getTask() {
            return new JobRun() {
                @Override
                void run(JobRunListener runListener) {
                    runListener.message("Running the test job...");
                }
            }
        }

        @Override
        String getDescription() {
            return "Test job";
        }

        @Override
        boolean isDisabled() {
            return false
        }
    }

    @Autowired
    private JobScheduler jobScheduler;

    @Autowired
    private JobListener jobListener;

    @Autowired
    private SettingsRepository settingsRepository;

    @Test
    void 'The paused status of a job is persisted'() {
        // Test job
        TestJob job = new TestJob("test-job");
        // Checks the job key
        assert job.key.toString() == '[test-category][test-type][test-job]'
        // Schedules the test job
        jobScheduler.schedule(job, Schedule.everyMinutes(60).after(60));
        // Checks it's not paused
        assert !settingsRepository.getBoolean(
                JobListener.class,
                '[test-category][test-type][test-job]',
                false
        ): "Job must not be paused"
        assert !jobListener.isPausedAtStartup(job.key): "Job must not be paused in listener"
        // Pauses the job
        jobScheduler.pause(job.key)
        // Checks it's paused
        assert settingsRepository.getBoolean(
                JobListener.class,
                '[test-category][test-type][test-job]',
                false
        ): "Job must be paused"
        assert jobListener.isPausedAtStartup(job.key): "Job must be paused in listener"
    }

    @Test
    void 'Job paused at startup'() {
        // Test job
        TestJob job = new TestJob("test-2");
        // Checks the job key
        assert job.key.toString() == '[test-category][test-type][test-2]'
        // Pauses it at startup
        settingsRepository.setBoolean(
                JobListener,
                '[test-category][test-type][test-2]',
                true
        )
        // Checks it's paused
        assert settingsRepository.getBoolean(
                JobListener.class,
                '[test-category][test-type][test-2]',
                false
        ): "Job must be paused"
        assert jobListener.isPausedAtStartup(job.key): "Job must be paused in listener"
        // Schedules the test job
        jobScheduler.schedule(job, Schedule.everyMinutes(60).after(60));
        // Checks it's paused
        def status = jobScheduler.getJobStatus(job.key)
        assert status.get().paused: "The job must be paused"
    }

}
