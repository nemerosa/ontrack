package net.nemerosa.ontrack.service.job

import net.nemerosa.ontrack.job.JobCategory
import net.nemerosa.ontrack.job.JobScheduler
import net.nemerosa.ontrack.job.JobStatus
import net.nemerosa.ontrack.job.Schedule
import org.junit.Test
import org.mockito.Mockito

import static org.mockito.Mockito.when

class JobMetricsTest {

    @Test
    void 'Metrics for a job'() {
        def jobScheduler = Mockito.mock(JobScheduler)
        when(jobScheduler.getJobStatuses()).thenReturn([
                new JobStatus(
                        1,
                        JobCategory.of('MyCategory').getType('MyType').getKey('MyKey'),
                        Schedule.EVERY_DAY,
                        "",
                        false,
                        true,
                        false,
                        false,
                        [:],
                        null,
                        2,
                        null,
                        200,
                        null,
                        5,
                        ""
                )
        ])

        def jobMetrics = new JobMetrics(jobScheduler)

        def metrics = jobMetrics.getTaggedMetrics()

        def range = (0..8)
        assert metrics.size() == range.size()
        range.each {
            assert metrics[it].tags == [
                    category: 'MyCategory',
                    type    : 'MyType',
                    jobId   : 'MyKey',
            ]
        }
    }

}
