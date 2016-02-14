package net.nemerosa.ontrack.model.support;

import net.nemerosa.ontrack.model.structure.NameDescription;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class ApplicationLogEntryTest {

    @Test
    public void details() {
        ApplicationLogEntry entry = ApplicationLogEntry.error(
                new RuntimeException("error"),
                NameDescription.nd("test", "Test"),
                "Test error")
                .withDetail("a", "A")
                .withDetail("b", "B")
                .withAuthentication("admin");
        assertEquals(ApplicationLogEntryLevel.ERROR, entry.getLevel());
        assertEquals(NameDescription.nd("test", "Test"), entry.getType());
        assertEquals("admin", entry.getAuthentication());
        assertEquals("Test error", entry.getInformation());
        assertEquals(Arrays.asList(
                NameDescription.nd("a", "A"),
                NameDescription.nd("b", "B")
        ), entry.getDetailList());
    }

}
