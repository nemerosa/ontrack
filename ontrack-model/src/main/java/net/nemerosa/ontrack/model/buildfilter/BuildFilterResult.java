package net.nemerosa.ontrack.model.buildfilter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class BuildFilterResult {

    private final boolean accept;
    private final boolean goingOn;

    public static BuildFilterResult stopNowIf(boolean condition) {
        return new BuildFilterResult(!condition, !condition);
    }

    public static BuildFilterResult stopNow() {
        return stopNowIf(true);
    }

    public static BuildFilterResult ok() {
        return new BuildFilterResult(true, true);
    }

    public BuildFilterResult acceptIf(boolean condition) {
        return new BuildFilterResult(accept && condition, goingOn);
    }

    public BuildFilterResult goOnIf(boolean condition) {
        return new BuildFilterResult(accept, goingOn && condition);
    }

    public BuildFilterResult forceAccept() {
        return new BuildFilterResult(true, goingOn);
    }
}
