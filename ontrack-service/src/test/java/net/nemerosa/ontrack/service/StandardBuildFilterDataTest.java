package net.nemerosa.ontrack.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.nemerosa.ontrack.model.structure.StandardBuildFilterData;
import org.junit.Test;

import java.time.LocalDate;

import static net.nemerosa.ontrack.json.JsonUtils.object;
import static net.nemerosa.ontrack.test.TestUtils.assertJsonRead;

public class StandardBuildFilterDataTest {

    @Test
    public void json_to_afterDate() throws JsonProcessingException {
        assertJsonRead(
                StandardBuildFilterData.of(2).withAfterDate(LocalDate.of(2014, 7, 14)),
                object()
                        .with("count", "2")
                        .with("afterDate", "2014-07-14")
                        .end(),
                StandardBuildFilterData.class
        );
    }

}