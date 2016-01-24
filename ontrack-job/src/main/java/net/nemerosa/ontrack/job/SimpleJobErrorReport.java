package net.nemerosa.ontrack.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleJobErrorReport implements JobErrorReporter {

    private final Logger logger = LoggerFactory.getLogger(JobErrorReporter.class);

    public static final JobErrorReporter INSTANCE = new SimpleJobErrorReport();

    @Override
    public void onJobError(JobStatus jobStatus, Exception ex) {
        logger.error(
                String.format("[job][%s][%s] Error", jobStatus.getKey().getType(), jobStatus.getKey().getId()),
                ex
        );
    }
}
