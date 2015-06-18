package net.nemerosa.ontrack.model.structure;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.net.URI;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
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
    /**
     * String used as a hint to redirect the user to a page.
     *
     * @deprecated Use {@link #page} instead
     */
    @Deprecated
    private final String hint;

    private final int accuracy;

    /**
     * String used as a hint to redirect the user to a page.
     *
     * @deprecated Use {@link #SearchResult(String, String, URI, URI, int)} instead
     */
    @Deprecated
    public SearchResult(String title, String description, URI uri, String hint, int accuracy) {
        this(title, description, uri, null, hint, accuracy);
    }

    public SearchResult(String title, String description, URI uri, URI page, int accuracy) {
        this(title, description, uri, page, "", accuracy);
    }
}
