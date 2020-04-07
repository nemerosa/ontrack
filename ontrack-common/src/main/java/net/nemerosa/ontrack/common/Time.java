package net.nemerosa.ontrack.common;

import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.util.Date;
import java.util.Locale;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.time.temporal.ChronoField.*;
import static java.time.temporal.ChronoField.NANO_OF_SECOND;

public final class Time {

    private static final DateTimeFormatter TIME_STORAGE_FORMAT = new DateTimeFormatterBuilder()
            .appendValue(HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(MINUTE_OF_HOUR, 2)
            .appendLiteral(':')
            .appendValue(SECOND_OF_MINUTE, 2)
            .optionalStart()
            .appendFraction(NANO_OF_SECOND, 0, 4, true)
            .toFormatter(Locale.ENGLISH);

    public static final DateTimeFormatter DATE_TIME_STORAGE_FORMAT = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(ISO_LOCAL_DATE)
            .appendLiteral('T')
            .append(TIME_STORAGE_FORMAT)
            .toFormatter(Locale.ENGLISH);

    private Time() {
    }

    public static LocalDateTime now() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }

    public static LocalDateTime toLocalDateTime(ZonedDateTime zonedDateTime) {
        return zonedDateTime.withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
    }

    public static String forStorage(LocalDateTime time) {
        if (time == null) {
            return null;
        } else {
            return time.format(DATE_TIME_STORAGE_FORMAT);
        }
    }

    public static LocalDateTime fromStorage(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        } else {
            return LocalDateTime.parse(value, DATE_TIME_STORAGE_FORMAT.withZone(ZoneOffset.UTC));
        }
    }

    public static LocalDateTime from(Date date, LocalDateTime defaultValue) {
        if (date != null) {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneOffset.UTC);
        } else {
            return defaultValue;
        }
    }

    public static Date toJavaUtilDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
    }

    /**
     * Returns a UTC local date/time from an Epoch time in milliseconds
     *
     * @param epochMillis Epoch time in milliseconds
     * @return UTC local date time
     */
    public static LocalDateTime from(long epochMillis) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneOffset.UTC);
    }

    /**
     * Converts a date/time to Epoch time in milliseconds.
     *
     * @param time Epoch time in milliseconds
     * @return UTC local date time
     */
    public static long toEpochMillis(LocalDateTime time) {
        return time.toInstant(ZoneOffset.UTC).toEpochMilli();
    }
}
