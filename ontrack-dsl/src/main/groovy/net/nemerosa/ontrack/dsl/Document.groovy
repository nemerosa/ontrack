package net.nemerosa.ontrack.dsl

import net.nemerosa.ontrack.dsl.doc.DSL
import net.nemerosa.ontrack.dsl.doc.DSLMethod


@DSL("Definition for a document, for upload and download methods. See also <<dsl-usecases-images>>.")
class Document {

    private final String type
    private final byte[] content

    static final Document EMPTY = new Document("", new byte[0])

    Document(String type, byte[] content) {
        this.type = type
        this.content = content
    }

    @DSLMethod("Returns the MIME type of the document.")
    String getType() {
        return type
    }

    @DSLMethod("Returns the content of the document as an array of bytes.")
    byte[] getContent() {
        return content
    }

    @DSLMethod("Returns true is the document is empty and has no content.")
    boolean isEmpty() {
        return "".equals(type) || content == null || content.length == 0
    }

    static boolean isValid(Document document) {
        return document != null && !document.isEmpty()
    }

}
