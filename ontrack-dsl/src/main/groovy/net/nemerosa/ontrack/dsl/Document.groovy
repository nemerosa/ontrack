package net.nemerosa.ontrack.dsl


class Document {

    private final String type
    private final byte[] content

    static final Document EMPTY = new Document("", new byte[0])

    Document(String type, byte[] content) {
        this.type = type
        this.content = content
    }

    String getType() {
        return type
    }

    byte[] getContent() {
        return content
    }

    boolean isEmpty() {
        return "".equals(type) || content == null || content.length == 0
    }

    static boolean isValid(Document document) {
        return document != null && !document.isEmpty()
    }

}
