package net.nemerosa.ontrack.model.support;

import lombok.Data;

@Data
public class Page {

    private int offset = 0;
    private int count = 100;

    public Page() {
        this(0, 100);
    }

    public Page(int offset, int count) {
        this.offset = offset;
        this.count = count;
    }
}
