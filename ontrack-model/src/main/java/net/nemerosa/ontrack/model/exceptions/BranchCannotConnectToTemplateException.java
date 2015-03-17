package net.nemerosa.ontrack.model.exceptions;

public class BranchCannotConnectToTemplateException extends InputException {
    public BranchCannotConnectToTemplateException(String name) {
        super("Branch %s cannot be connected to a template, because it is a template definition or is already connected.", name);
    }
}
