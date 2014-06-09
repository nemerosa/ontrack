package net.nemerosa.ontrack.client;


public class ClientMessageException extends ClientException {

    private final String message;

    public ClientMessageException(Exception ex, String content) {
        super(ex, content);
        this.message = content;
    }

    public ClientMessageException(String content) {
        super(content);
        this.message = content;
    }

    @Override
    public String getMessage() {
        return message;
    }

}