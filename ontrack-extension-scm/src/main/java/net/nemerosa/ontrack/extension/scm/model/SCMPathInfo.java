package net.nemerosa.ontrack.extension.scm.model;

import lombok.Data;

@Data
public class SCMPathInfo {

    private final String type;
    private final String url;
    private final String branch;
    private final String commit;

}
