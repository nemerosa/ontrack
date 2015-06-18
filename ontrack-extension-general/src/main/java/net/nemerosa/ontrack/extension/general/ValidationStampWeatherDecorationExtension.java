package net.nemerosa.ontrack.extension.general;

import net.nemerosa.ontrack.extension.api.DecorationExtension;
import net.nemerosa.ontrack.extension.support.AbstractExtension;
import net.nemerosa.ontrack.model.structure.*;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

@Component
public class ValidationStampWeatherDecorationExtension extends AbstractExtension implements DecorationExtension {

    private final StructureService structureService;

    @Autowired
    public ValidationStampWeatherDecorationExtension(GeneralExtensionFeature extensionFeature, StructureService structureService) {
        super(extensionFeature);
        this.structureService = structureService;
    }

    @Override
    public EnumSet<ProjectEntityType> getScope() {
        return EnumSet.of(ProjectEntityType.VALIDATION_STAMP);
    }

    @Override
    public List<Decoration> getDecorations(ProjectEntity entity) {
        // Argument check
        Validate.isTrue(entity instanceof ValidationStamp, "Expecting validation stamp");
        // List of last five runs for this validation stamp
        List<ValidationRun> runs = structureService.getValidationRunsForValidationStamp(entity.getId(), 0, 5);
        // Keeps only the ones which are not passed
        long notPassed = runs.stream().filter(run -> !run.isPassed()).count();
        // Result
        Decoration decoration;
        if (notPassed == 0) {
            decoration = sunny();
        } else if (notPassed == 1) {
            decoration = sunAndClouds();
        } else if (notPassed == 2) {
            decoration = clouds();
        } else if (notPassed == 3) {
            decoration = rain();
        } else {
            decoration = storm();
        }
        // OK
        return Collections.singletonList(decoration);
    }

    private Decoration sunny() {
        return Decoration.of(this, "sunny", "Sunny (0 failure in the last 5 builds)");
    }

    private Decoration sunAndClouds() {
        return Decoration.of(this, "sunAndClouds", "Sun and clouds (1 failure in the last 5 builds)");
    }

    private Decoration clouds() {
        return Decoration.of(this, "clouds", "Clouds (2 failures in the last 5 builds)");
    }

    private Decoration rain() {
        return Decoration.of(this, "rain", "Rain (3 failures in the last 5 builds)");
    }

    private Decoration storm() {
        return Decoration.of(this, "storm", "Storm (4 failures or more in the last 5 builds)");
    }

}
