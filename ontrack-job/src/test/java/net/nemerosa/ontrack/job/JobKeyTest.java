package net.nemerosa.ontrack.job;

import net.nemerosa.ontrack.job.support.CountJob;
import net.nemerosa.ontrack.job.support.LongCountJob;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class JobKeyTest {

    @Test
    public void same_category() {
        JobKey key = Fixtures.TEST_CATEGORY.getType("xxx").getKey("xxx");
        assertTrue(key.sameCategory(Fixtures.TEST_CATEGORY));
        assertTrue(new CountJob().getKey().sameCategory(Fixtures.TEST_CATEGORY));
        assertTrue(new LongCountJob().getKey().sameCategory(Fixtures.TEST_CATEGORY));
    }

}
