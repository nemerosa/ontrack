package net.nemerosa.ontrack.model.structure;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.nemerosa.ontrack.json.JsonUtils;
import org.junit.Test;

import java.util.Arrays;

import static net.nemerosa.ontrack.json.JsonUtils.object;
import static net.nemerosa.ontrack.test.TestUtils.assertJsonRead;
import static net.nemerosa.ontrack.test.TestUtils.assertJsonWrite;

public class BuildLinkFormTest {

    @Test
    public void json_read() throws JsonProcessingException {
        assertJsonRead(
                new BuildLinkForm(
                        true,
                        Arrays.asList(
                                new BuildLinkFormItem("P1", "B1"),
                                new BuildLinkFormItem("P2", "B2")
                        )
                ),
                object()
                        .with("addOnly", true)
                        .with("links", JsonUtils.array()
                                .with(object()
                                        .with("project", "P1")
                                        .with("build", "B1")
                                        .end()
                                )
                                .with(object()
                                        .with("project", "P2")
                                        .with("build", "B2")
                                        .end()
                                )
                                .end()
                        )
                        .end(),
                BuildLinkForm.class
        );
    }

    @Test
    public void json_write() throws JsonProcessingException {
        assertJsonWrite(
                object()
                        .with("addOnly", true)
                        .with("links", JsonUtils.array()
                                .with(object()
                                        .with("project", "P1")
                                        .with("build", "B1")
                                        .end()
                                )
                                .with(object()
                                        .with("project", "P2")
                                        .with("build", "B2")
                                        .end()
                                )
                                .end()
                        )
                        .end(),
                new BuildLinkForm(
                        true,
                        Arrays.asList(
                                new BuildLinkFormItem("P1", "B1"),
                                new BuildLinkFormItem("P2", "B2")
                        )
                )
        );
    }

}
