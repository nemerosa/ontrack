package net.nemerosa.ontrack.job

class NOPJobDecorator : JobDecorator {

    override fun decorate(job: Job, task: Task): Task = task

    companion object {
        val INSTANCE: JobDecorator = NOPJobDecorator()
    }
}
