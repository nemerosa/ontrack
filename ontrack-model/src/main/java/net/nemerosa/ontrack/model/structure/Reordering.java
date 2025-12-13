package net.nemerosa.ontrack.model.structure;

import lombok.Data;

import java.util.List;

/**
 * @deprecated Will be removed in V5. Was used in the legacy UI.
 */
@Data
@Deprecated
public class Reordering {

    private final List<Integer> ids;

}
