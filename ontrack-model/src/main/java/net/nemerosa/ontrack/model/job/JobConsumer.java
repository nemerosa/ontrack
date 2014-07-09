package net.nemerosa.ontrack.model.job;

@FunctionalInterface
public interface JobConsumer {

    boolean accept(Job job);

}
