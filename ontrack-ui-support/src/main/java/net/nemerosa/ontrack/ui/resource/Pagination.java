package net.nemerosa.ontrack.ui.resource;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Wither;

import java.net.URI;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Pagination {

    public static final Pagination NONE = null;
    private final int offset;
    private final int limit;
    private final int total;
    @Wither
    private final URI prev;
    @Wither
    private final URI next;

    public static Pagination of(int offset, int limit, int total) {
        return new Pagination(
                offset,
                limit,
                total,
                null,
                null
        );
    }

}
