package net.nemerosa.ontrack.model.structure;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.net.URI;

@Data
public class SearchResult {

    private final String title;
    private final String description;
    /**
     * API access point
     */
    private final URI uri;
    /**
     * Web access point
     */
    private final URI page;

    private final double accuracy;

}
