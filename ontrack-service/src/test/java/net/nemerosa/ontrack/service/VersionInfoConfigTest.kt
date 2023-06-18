package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.model.support.VersionInfoConfig;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class VersionInfoConfigTest {

    @Test
    public void parseDate_null() throws Exception {
        assertNotNull(VersionInfoConfig.parseDate(null));
    }

    @Test
    public void parseDate_blank() throws Exception {
        assertNotNull(VersionInfoConfig.parseDate(""));
    }

    @Test
    public void parseDate_incorrect() throws Exception {
        assertNotNull(VersionInfoConfig.parseDate("2014-12-01"));
    }

    @Test
    public void parseDate_correct() throws Exception {
        assertEquals(
                LocalDateTime.of(2014, 7, 13, 8, 34, 30),
                VersionInfoConfig.parseDate("2014-07-13T08:34:30")
        );
    }
}