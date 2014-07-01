package net.nemerosa.ontrack.model.structure;

import lombok.Data;

import java.net.URI;

@Data
public class SearchResult {

    private final String title;
    private final String description;
    private final URI uri;
    /**
     * String used as a hint to redirect the user to a page.
     */
    private final String hint;
    private final int accuracy;

}
