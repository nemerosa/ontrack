package net.nemerosa.ontrack.model.structure;

import lombok.Data;

import java.util.List;

@Data
public class BuildLinkForm {

    private final boolean addOnly;
    private final List<BuildLinkFormItem> links;

}
