package net.nemerosa.ontrack.job;

import org.slf4j.Logger;

import java.util.function.Consumer;

public interface JobRunListener {

    void progress(JobRunProgress value);

    default Consumer<String> logger() {
        return s -> progress(JobRunProgress.message(s));
    }

    default void message(String pattern, Object... parameters) {
        progress(JobRunProgress.message(pattern, parameters));
    }

    static JobRunListener logger(Logger logger) {
        return value -> logger.debug(value.getText());
    }

    static JobRunListener out() {
        return value -> System.out.println(value.getText());
    }

}
