package net.nemerosa.ontrack.extension.svn.model;

import net.nemerosa.ontrack.common.BaseException;

public class BuildPathNotDefinedException extends BaseException {

    public BuildPathNotDefinedException() {
        super("The build path is not defined.");
    }

}
