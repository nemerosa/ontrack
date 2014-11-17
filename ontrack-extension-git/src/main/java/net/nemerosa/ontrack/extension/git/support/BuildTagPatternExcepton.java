package net.nemerosa.ontrack.extension.git.support;

import net.nemerosa.ontrack.common.BaseException;

public class BuildTagPatternExcepton extends BaseException {
    public BuildTagPatternExcepton(String pattern, String name) {
        super("The %s build name does not match tag pattern %s", name, pattern);
    }
}
