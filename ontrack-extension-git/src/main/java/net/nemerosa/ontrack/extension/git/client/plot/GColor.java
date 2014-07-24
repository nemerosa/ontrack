package net.nemerosa.ontrack.extension.git.client.plot;

import lombok.Data;

@Data
public class GColor {

    private final int index;

    public static GColor of(int index) {
        return new GColor(index);
    }
}
