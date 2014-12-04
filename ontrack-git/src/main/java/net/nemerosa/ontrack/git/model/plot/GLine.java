package net.nemerosa.ontrack.git.model.plot;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = false)
public class GLine extends AbstractGItem {

    private final GColor color;
    private final GPoint a;
    private final GPoint b;
    private final int width;

    public static GLine of(GColor color, GPoint a, GPoint b, int width) {
        return new GLine(color, a, b, width);
    }

    @Override
    public int getMaxX() {
        return Math.max(a.getMaxX(), b.getMaxX());
    }

    @Override
    public int getMaxY() {
        return Math.max(a.getMaxY(), b.getMaxY());
    }
}
