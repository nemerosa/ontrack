package net.nemerosa.ontrack.job;

import java.util.function.Consumer;

@FunctionalInterface
public interface JobRunListener {

    void progress(JobRunProgress value);

    default Consumer<String> logger() {
        return s -> progress(JobRunProgress.message(s));
    }

    default void message(String pattern, Object... parameters) {
        progress(JobRunProgress.message(pattern, parameters));
    }

}
