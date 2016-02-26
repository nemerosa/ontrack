package net.nemerosa.ontrack.model.extension;

import net.nemerosa.ontrack.common.BaseException;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

public class ExtensionCycleException extends BaseException {
    public ExtensionCycleException(List<List<String>> cycles) {
        super("There is a cycle of dependencies between the extensions:%n%s", displayCycles(cycles));
    }

    private static String displayCycles(List<List<String>> cycles) {
        return cycles.stream()
                .map(ExtensionCycleException::displayCycle)
                .collect(Collectors.joining("\n"));
    }

    private static String displayCycle(List<String> sub) {
        return StringUtils.join(sub, " -> ");
    }
}
