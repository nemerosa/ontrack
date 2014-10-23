package net.nemerosa.ontrack.model.exceptions;

import org.apache.commons.lang3.StringUtils;

import java.util.Set;

public class BranchTemplateInstanceMissingParametersException extends InputException {
    public BranchTemplateInstanceMissingParametersException(String name, Set<String> parameters) {
        super(
                "Branch template %s cannot be instantiated because of missing parameters: %s",
                name,
                StringUtils.join(parameters, ", ")
        );
    }
}
