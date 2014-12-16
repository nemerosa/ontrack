package net.nemerosa.ontrack.git.model.plot;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GPlot {

    private final List<GItem> items = new ArrayList<>();

    public GPlot add(GItem item) {
        items.add(item);
        return this;
    }

    public int getWidth() {
        int width = 0;
        for (GItem item : items) {
            width = Math.max(width, item.getMaxX());
        }
        return width;
    }

    public int getHeight() {
        int height = 0;
        for (GItem item : items) {
            height = Math.max(height, item.getMaxY());
        }
        return height;
    }
}
