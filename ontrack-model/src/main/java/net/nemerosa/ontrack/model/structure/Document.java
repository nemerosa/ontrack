package net.nemerosa.ontrack.model.structure;

import lombok.Data;

@Data
public class Document {

    private final String type;
    private final byte[] content;

}
