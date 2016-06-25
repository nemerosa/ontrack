package net.nemerosa.ontrack.extension.general;

import lombok.Data;
import net.nemerosa.ontrack.model.structure.PromotionRun;

import java.net.URI;
import java.util.List;

@Data
public class BuildLinkDecoration {

    private final String project;
    private final String build;
    private final URI uri;
    private final List<PromotionRun> promotionRuns;

}
