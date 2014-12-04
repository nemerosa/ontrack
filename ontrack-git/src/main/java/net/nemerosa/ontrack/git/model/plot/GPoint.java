package net.nemerosa.ontrack.git.model.plot;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = false)
public class GPoint extends AbstractGItem {

    private final int x;
    private final int y;

    public static GPoint of(int x, int y) {
        return new GPoint(x, y);
    }

    public GPoint ty(int offset) {
        return of(x, y + offset);
    }

    @Override
    public int getMaxX() {
        return x;
    }

    @Override
    public int getMaxY() {
        return y;
    }
}
