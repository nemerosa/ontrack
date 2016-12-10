package net.nemerosa.ontrack.boot.support;

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class LocalDateTimeConverterTest {

    private final LocalDateTimeConverter converter = new LocalDateTimeConverter();

    @Test
    public void null_to_null() {
        assertNull(converter.convert(null));
    }

    @Test
    public void empty_to_null() {
        assertNull(converter.convert(""));
    }

    @Test
    public void blank_to_null() {
        assertNull(converter.convert(" "));
    }

    @Test
    public void utc_with_millis() {
        LocalDateTime t = converter.convert("2016-12-10T19:49:55.100Z");
        assertEquals(2016, t.getYear());
        assertEquals(12, t.getMonth().getValue());
        assertEquals(10, t.getDayOfMonth());
        assertEquals(19, t.getHour());
        assertEquals(49, t.getMinute());
        assertEquals(55, t.getSecond());
        assertEquals(100, t.get(ChronoField.MILLI_OF_SECOND));
    }

    @Test
    public void utc_with_seconds() {
        LocalDateTime t = converter.convert("2016-12-10T19:49:55Z");
        assertEquals(2016, t.getYear());
        assertEquals(12, t.getMonth().getValue());
        assertEquals(10, t.getDayOfMonth());
        assertEquals(19, t.getHour());
        assertEquals(49, t.getMinute());
        assertEquals(55, t.getSecond());
        assertEquals(0, t.get(ChronoField.MILLI_OF_SECOND));
    }

}
