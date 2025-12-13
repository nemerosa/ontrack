package net.nemerosa.ontrack.job

fun interface JobDecorator {
    /**
     * Decorates a task and returns a new one.
     */
    fun decorate(job: Job, task: Task): Task
}
