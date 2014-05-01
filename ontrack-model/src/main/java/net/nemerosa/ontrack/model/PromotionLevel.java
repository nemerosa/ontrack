package net.nemerosa.ontrack.model;

import lombok.Data;

@Data
public class PromotionLevel implements Comparable<PromotionLevel> {

    private final String id;
    private final String name;
    private final int order;
    private final String description;

    @Override
    public int compareTo(PromotionLevel o) {
        return this.order - o.order;
    }
}
