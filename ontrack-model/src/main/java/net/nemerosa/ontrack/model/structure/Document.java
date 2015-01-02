package net.nemerosa.ontrack.model.structure;

import lombok.Data;

@Data
public class Document {

    private final String type;
    private final byte[] content;

    public final static Document EMPTY = new Document("", new byte[0]);

    public boolean isEmpty() {
        return "".equals(type) || content == null || content.length == 0;
    }

}
