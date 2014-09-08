package net.nemerosa.ontrack.service;

import lombok.Data;
import net.nemerosa.ontrack.model.buildfilter.BuildFilter;
import net.nemerosa.ontrack.model.buildfilter.BuildFilterResult;
import net.nemerosa.ontrack.model.exceptions.PropertyTypeNotFoundException;
import net.nemerosa.ontrack.model.structure.*;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.function.Supplier;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Data
public class StandardBuildFilter implements BuildFilter {

    private final StandardBuildFilterData data;
    private final PropertyService propertyService;

    @Override
    public BuildFilterResult filter(List<Build> builds, Branch branch, Build build, Supplier<BuildView> buildViewSupplier) {
        // Count test
        if (builds.size() >= data.getCount()) {
            return BuildFilterResult.stopNow();
        }
        // Result by default is: accept and go on
        BuildFilterResult result = BuildFilterResult.ok();
        // After date
        if (data.getAfterDate() != null) {
            result = result.acceptIf(
                    build.getSignature().getTime().toLocalDate().compareTo(data.getAfterDate()) >= 0
            );
        }
        // Before date
        if (data.getBeforeDate() != null) {
            result = result.acceptIf(
                    build.getSignature().getTime().toLocalDate().compareTo(data.getBeforeDate()) <= 0
            );
        }
        // With promotion level
        if (isNotBlank(data.getWithPromotionLevel())) {
            result = result.acceptIf(
                    buildViewSupplier.get().getPromotionRuns().stream()
                            .filter(run -> data.getWithPromotionLevel().equals(run.getPromotionLevel().getName()))
                            .findAny()
                            .isPresent()
            );
        }
        // Since promotion level
        if (isNotBlank(data.getSincePromotionLevel())) {
            boolean promoted = buildViewSupplier.get().getPromotionRuns().stream()
                    .filter(run -> data.getSincePromotionLevel().equals(run.getPromotionLevel().getName()))
                    .findAny()
                    .isPresent();
            // The last build is accepted
            if (promoted) {
                result = result.forceAccept();
            }
            // Going on if not promoted
            result = result.goOnIf(!promoted);
        }
        // With validation stamp
        if (isNotBlank(data.getWithValidationStamp())) {
            result = result.acceptIf(
                    buildViewSupplier.get().getValidationStampRunViews().stream()
                            .filter(validationStampRunView -> hasValidationStamp(validationStampRunView, data.getWithValidationStamp(), data.getWithValidationStampStatus()))
                            .findAny()
                            .isPresent()
            );
        }
        // Since validation stamp
        if (isNotBlank(data.getSinceValidationStamp())) {
            boolean validated = buildViewSupplier.get().getValidationStampRunViews().stream()
                    .filter(validationStampRunView -> hasValidationStamp(validationStampRunView, data.getSinceValidationStamp(), data.getSinceValidationStampStatus()))
                    .findAny()
                    .isPresent();
            // The last build is accepted
            if (validated) {
                result = result.forceAccept();
            }
            // Going on if not validated
            result = result.goOnIf(!validated);
        }
        // Since property
        if (isNotBlank(data.getSinceProperty())) {
            result = result.goOnIf(
                    !hasProperty(build, data.getSinceProperty(), data.getSincePropertyValue())
            );
        }
        // With property
        if (isNotBlank(data.getWithProperty())) {
            result = result.acceptIf(
                    hasProperty(build, data.getWithProperty(), data.getWithPropertyValue())
            );
        }
        // OK
        return result;
    }

    private boolean hasProperty(Build build, String propertyTypeName, String propertyValue) {
        try {
            Property<?> property = propertyService.getProperty(build, propertyTypeName);
            return !property.isEmpty()
                    && (
                    StringUtils.isBlank(propertyValue)
                            || property.containsValue(propertyValue));
        } catch (PropertyTypeNotFoundException ex) {
            return false;
        }
    }

    private boolean hasValidationStamp(ValidationStampRunView validationStampRunView, String name, String status) {
        return (StringUtils.equals(name, validationStampRunView.getValidationStamp().getName()))
                && validationStampRunView.isRun()
                && (
                StringUtils.isBlank(status)
                        || StringUtils.equals(status, validationStampRunView.getLastStatus().getStatusID().getId())
        );
    }

}
