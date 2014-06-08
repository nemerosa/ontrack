package net.nemerosa.ontrack.extension.general;

import net.nemerosa.ontrack.extension.api.DecorationExtension;
import net.nemerosa.ontrack.extension.support.AbstractExtension;
import net.nemerosa.ontrack.model.structure.*;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    public Decoration getDecoration(ProjectEntity entity) {
        // Argument check
        Validate.isTrue(entity instanceof ValidationStamp, "Expecting validation stamp");
        // List of last five runs for this validation stamp
        List<ValidationRun> runs = structureService.getValidationRunsForValidationStamp(entity.getId(), 0, 5);
        // Keeps only the ones which are not passed
        long notPassed = runs.stream().filter(run -> !run.isPassed()).count();
        // Result
        if (notPassed == 0) {
            return sunny();
        } else if (notPassed == 1) {
            return sunAndClouds();
        } else if (notPassed == 2) {
            return clouds();
        } else if (notPassed == 3) {
            return rain();
        } else {
            return storm();
        }
    }

    private Decoration sunny() {
        return weather("Sunny (0 failure in the last 5 builds)", "sunny");
    }

    private Decoration sunAndClouds() {
        return weather("Sun and clouds (1 failure in the last 5 builds)", "sunAndClouds");
    }

    private Decoration clouds() {
        return weather("Clouds (2 failures in the last 5 builds)", "clouds");
    }

    private Decoration rain() {
        return weather("Rain (3 failures in the last 5 builds)", "rain");
    }

    private Decoration storm() {
        return weather("Storm (4 failures or more in the last 5 builds)", "storm");
    }

    private Decoration weather(String title, String icon) {
        return Decoration.of(title)
                .withIconPath(String.format("extension/weather/%s.png", icon));
    }

}
