package net.nemerosa.ontrack.repository;

import org.junit.Test;

import java.time.LocalDateTime;

import static net.nemerosa.ontrack.repository.AbstractJdbcRepository.dateTimeForDB;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class AbstractJdbcRepositoryTest {

    @Test
    public void dateTimeForDB_null() {
        assertNull(dateTimeForDB(null));
    }

    @Test
    public void dateTimeForDB_ok() {
        assertEquals("2014-05-22T22:07:10", dateTimeForDB(LocalDateTime.of(2014, 5, 22, 22, 7, 10)));
    }

}
