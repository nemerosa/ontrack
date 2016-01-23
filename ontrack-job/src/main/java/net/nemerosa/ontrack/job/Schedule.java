package net.nemerosa.ontrack.job;

import lombok.Data;

import java.util.concurrent.TimeUnit;

@Data
public class Schedule {

    private final long period;
    private final TimeUnit unit;

    public long toMiliseconds() {
        return TimeUnit.MILLISECONDS.convert(period, unit);
    }

    public static Schedule everySeconds(long seconds) {
        return new Schedule(seconds, TimeUnit.SECONDS);
    }

    public static final Schedule EVERY_SECOND = everySeconds(1);
}
