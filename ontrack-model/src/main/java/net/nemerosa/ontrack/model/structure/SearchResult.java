package net.nemerosa.ontrack.model.structure;

import lombok.Data;

import java.net.URI;

@Data
public class SearchResult {

    private final String title;
    private final String description;
    private final URI uri;
    private final int accuracy;

}
