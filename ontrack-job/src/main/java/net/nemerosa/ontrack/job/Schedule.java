package net.nemerosa.ontrack.job;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.TimeUnit;

@Data
public class Schedule {

    private final long initialPeriod;
    private final long period;
    private final TimeUnit unit;

    public String getPeriodText() {
        if (period <= 0) {
            return "Manually";
        } else if (period == 1) {
            return "Every " + StringUtils.substringBeforeLast(unit.name().toLowerCase(), "s");
        } else {
            return "Every " + period + " " + unit.name().toLowerCase();
        }
    }

    public long toMiliseconds() {
        return TimeUnit.MILLISECONDS.convert(period, unit);
    }

    public static Schedule everySeconds(long seconds) {
        return new Schedule(0, seconds, TimeUnit.SECONDS);
    }

    public static Schedule everyMinutes(long minutes) {
        return new Schedule(0, minutes, TimeUnit.MINUTES);
    }

    public static final Schedule NONE = everySeconds(0);
    public static final Schedule EVERY_SECOND = everySeconds(1);
    public static final Schedule EVERY_MINUTE = everyMinutes(1);
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

    public Schedule convertTo(TimeUnit target) {
        return new Schedule(
                target.convert(initialPeriod, unit),
                target.convert(period, unit),
                target
        );
    }
}
