package net.nemerosa.ontrack.job;

@FunctionalInterface
public interface JobDecorator {

    /**
     * Decorates a task and returns a new one.
     */
    Runnable decorate(Job job, Runnable task);

}
