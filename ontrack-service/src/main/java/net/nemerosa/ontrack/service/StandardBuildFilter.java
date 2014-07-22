package net.nemerosa.ontrack.service;

import lombok.Data;
import net.nemerosa.ontrack.model.buildfilter.BuildFilter;
import net.nemerosa.ontrack.model.buildfilter.BuildFilterResult;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.Build;
import net.nemerosa.ontrack.model.structure.BuildView;
import net.nemerosa.ontrack.model.structure.ValidationStampRunView;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.function.Supplier;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Data
public class StandardBuildFilter implements BuildFilter {

    private final StandardBuildFilterData data;


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
            result = result.goOnIf(
                    !buildViewSupplier.get().getPromotionRuns().stream()
                            .filter(run -> data.getSincePromotionLevel().equals(run.getPromotionLevel().getName()))
                            .findAny()
                            .isPresent()
            );
        }
        // Since validation stamp
        if (data.getSinceValidationStamp() != null) {
            String vsName = data.getSinceValidationStamp().getName();
            if (isNotBlank(vsName)) {
                result = result.goOnIf(
                        !buildViewSupplier.get().getValidationStampRunViews().stream()
                                .filter(validationStampRunView -> hasValidationStamp(validationStampRunView, data.getSinceValidationStamp()))
                                .findAny()
                                .isPresent()
                );
            }
        }
        // TODO withValidationStamp
        // TODO withProperty
        // OK
        return result;
    }

    private boolean hasValidationStamp(ValidationStampRunView validationStampRunView, ValidationStampFilter filter) {
        return (StringUtils.equals(filter.getName(), validationStampRunView.getValidationStamp().getName()))
                && validationStampRunView.isRun()
                && (
                StringUtils.isBlank(filter.getStatus())
                        || StringUtils.equals(filter.getStatus(), validationStampRunView.getLastStatus().getStatusID().getId())
        );
    }

}
