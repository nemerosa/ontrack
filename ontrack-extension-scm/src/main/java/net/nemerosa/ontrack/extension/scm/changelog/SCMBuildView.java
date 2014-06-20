package net.nemerosa.ontrack.extension.scm.changelog;

import lombok.Data;
import net.nemerosa.ontrack.model.structure.BuildView;

/**
 * Annotation of a {@link net.nemerosa.ontrack.model.structure.BuildView} with some specific data for the SCM.
 */
@Data
public class SCMBuildView<T> {

    private final BuildView buildView;
    private final T scm;

}
