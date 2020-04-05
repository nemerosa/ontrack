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

    public static Signature anonymous() {
        return new Signature(
                Time.now(),
                User.anonymous()
        );
    }

    /**
     * @deprecated Use {@link #anonymous()} method instead. This method will be deleted in version 4.
     */
    public static Signature none() {
        return anonymous();
    }

    public Signature withTime(LocalDateTime dateTime) {
        return new Signature(dateTime != null ? dateTime : Time.now(), user);
    }
}
