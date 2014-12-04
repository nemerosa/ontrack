package net.nemerosa.ontrack.git.model.plot;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = false)
public class GOval extends AbstractGItem {

    private final GColor color;
    private final GPoint c;
    private final GDim d;

    public static GOval of(GColor color, GPoint c, GDim d) {
        return new GOval(color, c, d);
    }

    @Override
    public int getMaxX() {
        return c.getX() + d.getW();
    }

    @Override
    public int getMaxY() {
        return c.getY() + d.getH();
    }
}
