package net.nemerosa.ontrack.model;

import lombok.Data;

import java.util.Optional;

@Data
public class Ack {

    public static final Ack OK = new Ack(true);
    public static final Ack NOK = new Ack(false);

    public static Ack validate(boolean test) {
        return test ? OK : NOK;
    }

    public static Ack validate(Optional<?> optional) {
        return optional != null ? validate(optional.isPresent()) : NOK;
    }

    public static Ack one(int count) {
        return validate(count == 1);
    }

    private final boolean success;

    public Ack and(Ack ack) {
        return validate(isSuccess() && ack.isSuccess());
    }

    public Ack or(Ack ack) {
        return validate(isSuccess() || ack.isSuccess());
    }

}
