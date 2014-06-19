package net.nemerosa.ontrack.model.support;

import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public final class Time {

    public static final DateTimeFormatter DATE_TIME_STORAGE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private Time() {
    }

    public static LocalDateTime now() {
        return LocalDateTime.now(ZoneOffset.UTC);
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
}
