package net.nemerosa.ontrack.boot.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.nemerosa.ontrack.json.JsonUtils;
import net.nemerosa.ontrack.model.Branch;
import net.nemerosa.ontrack.test.TestUtils;
import org.junit.Test;

public class ResourceTest {

    @Test
    public void to_json_only_data() throws JsonProcessingException {
        Branch branch = new Branch("b", "B1", "Branche 1");
        TestUtils.assertJsonWrite(
                JsonUtils.object()
                        .with("id", "b")
                        .with("name", "B1")
                        .with("description", "Branche 1")
                        .end(),
                Resource.of(branch)
        );
    }

}
