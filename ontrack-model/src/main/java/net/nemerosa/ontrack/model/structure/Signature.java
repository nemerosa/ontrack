package net.nemerosa.ontrack.model.structure;

import lombok.Data;
import net.nemerosa.ontrack.common.Time;

import java.time.LocalDateTime;

@Data
public class Signature {

    private final LocalDateTime time;
    private final User user;

    public static Signature of(String name) {
        return of(Time.now(), name);
    }

    public static Signature of(LocalDateTime dateTime, String name) {
        return new Signature(
                dateTime,
                User.of(name)
        );
    }

    public static Signature none() {
        return new Signature(
                Time.now(),
                User.anonymous()
        );
    }

    public Signature withTime(LocalDateTime dateTime) {
        return new Signature(dateTime != null ? dateTime : Time.now(), user);
    }
}
