package net.nemerosa.ontrack.common;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UtilsTest {

    @Test
    public void asList_null() {
        assertTrue(Utils.asList(null).isEmpty());
    }

    @Test
    public void asList_blank() {
        assertTrue(Utils.asList("").isEmpty());
    }

    @Test
    public void asList_one() {
        assertEquals(
                Arrays.asList("Test"),
                Utils.asList("Test")
        );
    }

    @Test
    public void asList_two() {
        assertEquals(
                Arrays.asList("Test", "Second"),
                Utils.asList("Test\nSecond")
        );
    }

}