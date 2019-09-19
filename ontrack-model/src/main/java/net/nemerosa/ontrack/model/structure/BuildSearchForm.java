package net.nemerosa.ontrack.model.structure;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Wither;
import net.nemerosa.ontrack.model.annotations.API;
import net.nemerosa.ontrack.model.annotations.APIDescription;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class BuildSearchForm {

    @Min(1)
    @Wither
    @APIDescription("Maximum number of builds to return.")
    private int maximumCount = 10;
    @Wither
    @APIDescription("Regular expression to match against the branch name.")
    private String branchName;
    @Wither
    @APIDescription("Regular expression to match against the build name, unless `buildExactMatch` is set to `true`.")
    private String buildName;
    @Wither
    @APIDescription("Matches a build having at least this promotion.")
    private String promotionName;
    @Wither
    @APIDescription("Matches a build having at least this validation with PASSED as a status.")
    private String validationStampName;
    @Wither
    @APIDescription("Matches a build having this property.")
    private String property;
    @Size(max = 200)
    @Wither
    @APIDescription("When `property` is set, matches against the property value.")
    private String propertyValue;
    @Wither
    @APIDescription("When `buildName` is set, considers an exact match on the build name.")
    private boolean buildExactMatch;
    @Wither
    @APIDescription("`project:build` expression, matches against builds being linked from the build to match.")
    private String linkedFrom;
    @Wither
    @APIDescription("`project:build` expression, matches against builds being linked to the build to match.")
    private String linkedTo;

    public BuildSearchForm() {
    }
}
