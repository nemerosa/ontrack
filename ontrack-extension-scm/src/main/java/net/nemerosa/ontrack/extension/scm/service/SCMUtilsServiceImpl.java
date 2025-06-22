package net.nemerosa.ontrack.extension.scm.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@Service
public class SCMUtilsServiceImpl implements SCMUtilsService {

    @Override
    public Predicate<String> getPathFilter(List<String> patterns) {
        if (patterns == null || patterns.isEmpty()) {
            return path -> true;
        } else {
            return path -> patterns.stream()
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
                    ).anyMatch(
                            pattern -> pattern.matcher(path).matches()
                    );
        }
    }

}
