package net.nemerosa.ontrack.ui.resource;

import lombok.Data;

import java.net.URI;

@Data
public class Pagination {

    private final int offset;
    private final int limit;
    private final int total;
    private final URI prev;
    private final URI next;

}
