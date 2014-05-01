package net.nemerosa.ontrack.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Signature {

    private final LocalDateTime time;
    private final User user;

}
