package net.nemerosa.ontrack.model.exceptions;

import org.apache.commons.lang3.StringUtils;

import java.util.Set;

public class BranchTemplateInstanceUnknownParametersException extends InputException {
    public BranchTemplateInstanceUnknownParametersException(String name, Set<String> parameters) {
        super(
                "Branch template %s cannot be instantiated because of unknown parameters: %s",
                name,
                StringUtils.join(parameters, ", ")
        );
    }
}
