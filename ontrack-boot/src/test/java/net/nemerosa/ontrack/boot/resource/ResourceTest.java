package net.nemerosa.ontrack.boot.resource;

import net.nemerosa.ontrack.model.Branch;
import org.junit.Test;

public class ResourceTest {

    @Test
    public void to_json_only_data() {
        Branch branch = new Branch("b", "B1", "Branche 1");

    }

}
