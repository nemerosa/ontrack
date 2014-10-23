package net.nemerosa.ontrack.model.structure;

import java.util.List;

public interface TemplateSynchronisationService {

    /**
     * List of template synchronisation sources
     */
    List<TemplateSynchronisationSource<?>> getSynchronisationSources();

    /**
     * Gets a sync. source using its ID.
     */
    TemplateSynchronisationSource<?> getSynchronisationSource(String id);
}
