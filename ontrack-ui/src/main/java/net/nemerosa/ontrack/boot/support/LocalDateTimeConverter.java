package net.nemerosa.ontrack.boot.support;

import net.nemerosa.ontrack.common.Time;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Component
public class LocalDateTimeConverter implements Converter<String, LocalDateTime> {
    @Override
    public LocalDateTime convert(String s) {
        if (StringUtils.isBlank(s)) {
            return null;
        } else {
            // Parses as a zoned date time
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(s);
            // Returns in local application time (UTC)
            return Time.toLocalDateTime(zonedDateTime);
        }
    }
}
