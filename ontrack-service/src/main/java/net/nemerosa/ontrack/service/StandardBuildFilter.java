package net.nemerosa.ontrack.service;

import lombok.Data;
import net.nemerosa.ontrack.model.buildfilter.BuildFilter;
import net.nemerosa.ontrack.model.buildfilter.BuildFilterResult;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.model.support.PropertyServiceHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.function.Supplier;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Data
@Deprecated
public class StandardBuildFilter implements BuildFilter {

    private final StandardBuildFilterData data;
    private final PropertyService propertyService;
    private final StructureService structureService;

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
                            .anyMatch(run -> data.getWithPromotionLevel().equals(run.getPromotionLevel().getName()))
            );
        }
        // Since promotion level
        if (isNotBlank(data.getSincePromotionLevel())) {
            boolean promoted = buildViewSupplier.get().getPromotionRuns().stream()
                    .anyMatch(run -> data.getSincePromotionLevel().equals(run.getPromotionLevel().getName()));
            // The last build is accepted
            if (promoted) {
                result = result.doAccept();
            }
            // Going on if not promoted
            result = result.goOnIf(!promoted);
        }
        // With validation stamp
        if (isNotBlank(data.getWithValidationStamp())) {
            result = result.acceptIf(
                    buildViewSupplier.get().getValidationStampRunViews().stream()
                            .anyMatch(validationStampRunView -> hasValidationStamp(validationStampRunView, data.getWithValidationStamp(), data.getWithValidationStampStatus()))
            );
        }
        // Since validation stamp
        if (isNotBlank(data.getSinceValidationStamp())) {
            boolean validated = buildViewSupplier.get().getValidationStampRunViews().stream()
                    .anyMatch(validationStampRunView -> hasValidationStamp(validationStampRunView, data.getSinceValidationStamp(), data.getSinceValidationStampStatus()));
            // The last build is accepted
            if (validated) {
                result = result.doAccept();
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
        // Linked from
        String linkedFrom = data.getLinkedFrom();
        if (isNotBlank(linkedFrom)) {
            String project = StringUtils.substringBefore(linkedFrom, ":");
            String buildPattern = StringUtils.substringAfter(linkedFrom, ":");
            result = result.acceptIf(structureService.isLinkedFrom(build, project, buildPattern));
        }
        // Linked to
        String linkedTo = data.getLinkedTo();
        if (isNotBlank(linkedTo)) {
            String project = StringUtils.substringBefore(linkedTo, ":");
            String buildPattern = StringUtils.substringAfter(linkedTo, ":");
            result = result.acceptIf(structureService.isLinkedTo(build, project, buildPattern));
        }
        // OK
        return result;
    }

    private boolean hasProperty(Build build, String propertyTypeName, String propertyValue) {
        return PropertyServiceHelper.hasProperty(propertyService, build, propertyTypeName, propertyValue);
    }

    private boolean hasValidationStamp(ValidationStampRunView validationStampRunView, String name, String status) {
        return validationStampRunView.hasValidationStamp(name, status);
    }

}
