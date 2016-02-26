package net.nemerosa.ontrack.job.support;

import net.nemerosa.ontrack.job.*;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ParamJob implements Job {

    private final AtomicInteger count = new AtomicInteger();
    private final AtomicReference<String> value = new AtomicReference<>();

    @Override
    public JobKey getKey() {
        return Fixtures.TEST_CATEGORY.getType("param").getKey("param");
    }

    public int getCount() {
        return count.get();
    }

    public String getValue() {
        return value.get();
    }

    @Override
    public JobRun getTask() {
        return (listener) -> {
            count.incrementAndGet();
            String param = listener.<String>getParam("text").orElse(null);
            value.set(param);
            listener.progress(JobRunProgress.message("Count = %s", count.get()));
            listener.progress(JobRunProgress.message("Value = %s", value.get()));
        };
    }

    @Override
    public String getDescription() {
        return "Param";
    }

    @Override
    public boolean isDisabled() {
        return false;
    }
}
