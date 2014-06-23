package net.nemerosa.ontrack.extension.jira.client;

import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

public class JIRAClientImplTest {

    @Test
    public void parseFromJIRA() {
        LocalDateTime ldt = JIRAClientImpl.parseFromJIRA("2014-06-05T14:39:51.943+0000");
        assertEquals(2014, ldt.getYear());
        assertEquals(6, ldt.getMonthValue());
        assertEquals(5, ldt.getDayOfMonth());
        assertEquals(14, ldt.getHour());
        assertEquals(51, ldt.getSecond());
    }

}
