package net.nemerosa.ontrack.model.structure;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Wither;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class BuildSearchForm {

    @Min(1)
    @Wither
    private int maximumCount = 10;
    @Wither
    private String branchName;
    @Wither
    private String buildName;
    @Wither
    private String promotionName;
    @Wither
    private String validationStampName;
    @Wither
    private String property;
    @Size(max = 200)
    @Wither
    private String propertyValue;
    @Wither
    private boolean buildExactMatch;
    @Wither
    private String linkedFrom;
    @Wither
    private String linkedTo;

    public BuildSearchForm() {
    }
}
