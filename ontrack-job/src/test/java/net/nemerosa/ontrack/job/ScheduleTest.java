package net.nemerosa.ontrack.job;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ScheduleTest {

    @Test
    public void display_manual() {
        assertEquals("Manually", Schedule.everyMinutes(0).getPeriodText());
    }

    @Test
    public void display_one_minute() {
        assertEquals("Every minute", Schedule.everyMinutes(1).getPeriodText());
    }

    @Test
    public void display_several_minute() {
        assertEquals("Every 10 minutes", Schedule.everyMinutes(10).getPeriodText());
    }

}
