package net.nemerosa.ontrack.model.structure;

import lombok.Data;

import javax.validation.constraints.Min;

@Data
public class BuildSearchForm {

    @Min(1)
    private int maximumCount = 10;
    private String branchName;
    private String buildName;
    private String promotionName;
    private String validationStampName;

}
