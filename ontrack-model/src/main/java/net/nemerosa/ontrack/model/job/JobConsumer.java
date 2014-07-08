package net.nemerosa.ontrack.model.job;

@FunctionalInterface
public interface JobConsumer {

    void accept(Job job);

}
