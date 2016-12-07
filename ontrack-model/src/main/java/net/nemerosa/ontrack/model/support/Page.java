package net.nemerosa.ontrack.model.support;

import lombok.Data;

import java.util.List;

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

    /**
     * Extracts a sublist using pagination information
     *
     * @param list Initial list
     * @param <T>  Type of elements in the list
     * @return Sub list
     */
    public <T> List<T> extract(List<T> list) {
        int toIndex = Math.min(
                offset + count,
                list.size()
        );
        return list.subList(
                offset,
                toIndex
        );
    }
}
