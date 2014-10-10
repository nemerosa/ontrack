package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.model.structure.ExpressionEngine;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Default implementation for an expression engine, based on a Grovy sandbox
 */
public class ExpressionEngineImpl implements ExpressionEngine {

    public static final Pattern PATTERN = Pattern.compile("\\$\\{([.]+)\\}");

    @Override
    public String render(String template, Map<String, Object> parameters) {
        // Null handling
        if (template == null) {
            return null;
        }
        // Parsing and resolution
        Matcher matcher = PATTERN.matcher(template);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String expression = matcher.group(1);
            String resolution = resolve(expression, parameters);
            matcher.appendReplacement(buffer, resolution);
        }
        matcher.appendTail(buffer);
        // OK
        return buffer.toString();
    }

    public String resolve(String expression, Map<String, Object> parameters) {
        // FIXME Method net.nemerosa.ontrack.service.ExpressionEngineImpl.resolve
        return null;
    }
}
