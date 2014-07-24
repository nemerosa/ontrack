package net.nemerosa.ontrack.extension.git.client.plot;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class GDim {

    private final int w;
    private final int h;

    public static GDim of(int w, int h) {
        return new GDim(w, h);
    }
}
