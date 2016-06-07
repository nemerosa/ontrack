package net.nemerosa.ontrack.model.structure;

import java.util.List;
import java.util.Optional;

public interface TemplateSynchronisationService {

    /**
     * List of template synchronisation sources
     */
    List<TemplateSynchronisationSource<?>> getSynchronisationSources();

    /**
     * Gets a sync. source using its ID.
     */
    Optional<TemplateSynchronisationSource<?>> getSynchronisationSource(String id);
}
