package net.nemerosa.ontrack.model.structure;

import net.nemerosa.ontrack.model.Ack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface SearchService {

    Collection<SearchResult> search(SearchRequest request);

    /**
     * Makes sure all search indexes are initialized.
     */
    void indexInit();

    /**
     * Resetting all search indexes, optionally restoring them.
     * <p>
     * This method is mostly used for testing but could be used
     * to reset faulty indexes.
     *
     * @param reindex <code>true</code> to relaunch the indexation
     *                afterwards
     * @return OK if indexation was completed successfully
     */
    @NotNull
    Ack indexReset(boolean reindex);
}
