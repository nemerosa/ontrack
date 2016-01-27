package net.nemerosa.ontrack.model.job;

/**
 * This interface is used to collect information messages
 * from the running {@linkplain net.nemerosa.ontrack.model.job.Job jobs}.
 */
@FunctionalInterface
@Deprecated
public interface JobInfoListener {

    void post(String message);

}
