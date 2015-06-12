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
        return new BuildFilterResult(false, false);
    }

    public static BuildFilterResult ok() {
        return new BuildFilterResult(true, true);
    }

    public static BuildFilterResult notAccept() {
        return new BuildFilterResult(false, true);
    }

    public static BuildFilterResult accept() {
        return new BuildFilterResult(true, true);
    }

    public BuildFilterResult goingOn() {
        return new BuildFilterResult(accept, true);
    }

    public BuildFilterResult stop() {
        return new BuildFilterResult(accept, false);
    }

    public BuildFilterResult acceptIf(boolean condition) {
        return new BuildFilterResult(accept && condition, goingOn);
    }

    public BuildFilterResult goOnIf(boolean condition) {
        return new BuildFilterResult(accept, goingOn && condition);
    }

    public BuildFilterResult doAccept() {
        return new BuildFilterResult(true, goingOn);
    }
}
