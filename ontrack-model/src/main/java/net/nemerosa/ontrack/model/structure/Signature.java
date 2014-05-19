package net.nemerosa.ontrack.model.structure;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Signature {

    private final LocalDateTime time;
    private final User user;

    public static Signature of(String name) {
        return of(LocalDateTime.now(), name);
    }

    public static Signature of(LocalDateTime dateTime, String name) {
        return new Signature(
                dateTime,
                User.of(name)
        );
    }

    public static Signature none() {
        return new Signature(
                LocalDateTime.now(),
                User.anonymous()
        );
    }
}
