package net.nemerosa.ontrack.job;

public class NOPJobDecorator implements JobDecorator {

    public static final JobDecorator INSTANCE = new NOPJobDecorator();

    @Override
    public Runnable decorate(Job job, Runnable task) {
        return task;
    }
}
