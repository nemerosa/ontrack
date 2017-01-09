package net.nemerosa.ontrack.model.structure;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.time.LocalDateTime;

import static net.nemerosa.ontrack.json.JsonUtils.object;

public abstract class TestFixtures {

    public static final ObjectNode SIGNATURE_OBJECT = object()
            .with("time", "2016-12-27T21:10:00Z")
            .with("user", object()
                    .with("name", "test")
                    .end()
            )
            .end();

    public static final Signature SIGNATURE = Signature.of(
            LocalDateTime.of(2016, 12, 27, 21, 10),
            "test"
    );

}
