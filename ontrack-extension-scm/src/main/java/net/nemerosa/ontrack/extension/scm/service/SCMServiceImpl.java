package net.nemerosa.ontrack.extension.scm.service;

import net.nemerosa.ontrack.extension.scm.model.SCMChangeLogFile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class SCMServiceImpl implements SCMService {

    @Override
    public <T extends SCMChangeLogFile> String diff(List<T> changeLogFiles, List<String> patterns, Function<T, String> diffFn) {
        // Filter
        List<Pattern> regexList = patterns.stream()
                .map(pattern ->
                                Pattern.compile(
                                        "^" +
                                                pattern
                                                        .replace("**", "$MULTI$")
                                                        .replace("*", "$SINGLE$")
                                                        .replace("$SINGLE$", "[^\\/]+")
                                                        .replace("$MULTI$", ".*") +
                                                "$"
                                )
                )
                .collect(Collectors.toList());

        // Gets all changes
        return changeLogFiles.stream()
                // Filters on path
                .filter(changeLogFile -> regexList.stream().anyMatch(pattern -> pattern.matcher(changeLogFile.getPath()).matches()))
                        // Collects the diffs
                .map(diffFn)
                        // Group together
                .collect(Collectors.joining("\n"))
                ;
    }

}
