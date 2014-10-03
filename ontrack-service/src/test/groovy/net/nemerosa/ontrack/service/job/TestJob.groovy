package net.nemerosa.ontrack.service.job

import net.nemerosa.ontrack.model.job.Job
import net.nemerosa.ontrack.model.job.JobInfoListener
import net.nemerosa.ontrack.model.job.JobTask
import net.nemerosa.ontrack.model.job.RunnableJobTask

import java.util.concurrent.atomic.AtomicInteger

class TestJob implements Job {

    private final String category
    private final String id
    private final String description
    private final boolean disabled

    TestJob(String category, String id, String description, boolean disabled) {
        this.category = category
        this.id = id
        this.description = description
        this.disabled = disabled
    }

    private final AtomicInteger count = new AtomicInteger()

    static TestJob create() {
        create(1)
    }

    static TestJob create(int id) {
        create(1, false)
    }

    static TestJob create(int id, boolean disabled) {
        new TestJob(
                "Test",
                "$id",
                "Test $id",
                disabled
        )
    }

    @Override
    String getCategory() {
        category
    }

    @Override
    String getId() {
        id
    }

    @Override
    String getDescription() {
        description
    }

    @Override
    boolean isDisabled() {
        disabled
    }

    @Override
    int getInterval() {
        10
    }

    @Override
    JobTask createTask() {
        new RunnableJobTask({ infoListener -> run(infoListener) })
    }

    protected def run(JobInfoListener jobInfoListener) {
        jobInfoListener.post "Running: $id"
        count.incrementAndGet()
    }

    int getCount() {
        count.get()
    }
}
