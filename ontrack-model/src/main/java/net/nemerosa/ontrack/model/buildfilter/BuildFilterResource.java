package net.nemerosa.ontrack.model.buildfilter;

import lombok.Data;

import java.util.Map;

@Data
public class BuildFilterResource {

    private final String name;
    private final BuildFilterForm form;
    private final Map<String, String> filter;

}
