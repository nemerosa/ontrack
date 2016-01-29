package net.nemerosa.ontrack.job;

import lombok.Data;

import java.util.concurrent.TimeUnit;

@Data
public class Schedule {

    private final long initialPeriod;
    private final long period;
    private final TimeUnit unit;

    public long toMiliseconds() {
        return TimeUnit.MILLISECONDS.convert(period, unit);
    }

    public static Schedule everySeconds(long seconds) {
        return new Schedule(0, seconds, TimeUnit.SECONDS);
    }

    public static Schedule everyMinutes(long minutes) {
        return new Schedule(0, minutes, TimeUnit.MINUTES);
    }

    public static final Schedule EVERY_SECOND = everySeconds(1);
    public static final Schedule EVERY_MINUTE = everySeconds(60);
    public static final Schedule EVERY_DAY = new Schedule(0, 1, TimeUnit.DAYS);

    public Schedule after(int initial) {
        return new Schedule(
                initial,
                period,
                unit
        );
    }

    public boolean sameDelayThan(Schedule schedule) {
        return this.period == schedule.period && this.unit.equals(schedule.unit);
    }
}
