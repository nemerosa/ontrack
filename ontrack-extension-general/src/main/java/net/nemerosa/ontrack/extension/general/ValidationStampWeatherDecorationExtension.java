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
public class ValidationStampWeatherDecorationExtension extends AbstractExtension implements DecorationExtension<ValidationStampWeather> {

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
    public List<Decoration<ValidationStampWeather>> getDecorations(ProjectEntity entity) {
        // Argument check
        Validate.isTrue(entity instanceof ValidationStamp, "Expecting validation stamp");
        // List of last five runs for this validation stamp
        List<ValidationRun> runs = structureService.getValidationRunsForValidationStamp(entity.getId(), 0, 5);
        // Keeps only the ones which are not passed
        long notPassed = runs.stream().filter(run -> !run.isPassed()).count();
        // Result
        ValidationStampWeather weather;
        if (notPassed == 0) {
            weather = ValidationStampWeather.sunny;
        } else if (notPassed == 1) {
            weather = ValidationStampWeather.sunAndClouds;
        } else if (notPassed == 2) {
            weather = ValidationStampWeather.clouds;
        } else if (notPassed == 3) {
            weather = ValidationStampWeather.rain;
        } else {
            weather = ValidationStampWeather.storm;
        }
        // OK
        return Collections.singletonList(Decoration.of(this, weather));
    }
}
