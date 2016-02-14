package net.nemerosa.ontrack.job;

import net.nemerosa.ontrack.job.JobCategory;

public interface Fixtures {

    JobCategory TEST_CATEGORY = JobCategory.of("test").withName("Test");
    JobCategory TEST_OTHER_CATEGORY = JobCategory.of("test-other").withName("Other tests");

}
