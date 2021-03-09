package net.nemerosa.ontrack.extension.general;

import lombok.Data;

@Data
public class MessageProperty {

    private final MessageType type;
    private final String text;

}
