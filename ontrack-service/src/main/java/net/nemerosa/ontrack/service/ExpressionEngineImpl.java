package net.nemerosa.ontrack.service;

import groovy.lang.Binding;
import groovy.lang.GString;
import groovy.lang.GroovyShell;
import net.nemerosa.ontrack.model.structure.ExpressionEngine;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.SecureASTCustomizer;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Default implementation for an expression engine, based on a Grovy sandbox
 */
@Component
public class ExpressionEngineImpl implements ExpressionEngine {

    public static final Pattern PATTERN = Pattern.compile("\\$\\{(.+)\\}");

    @Override
    public String render(String template, Map<String, ?> parameters) {
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

    public String resolve(String expression, Map<String, ?> parameters) {

        SecureASTCustomizer secure = new SecureASTCustomizer();
        secure.setClosuresAllowed(false);
        secure.setMethodDefinitionAllowed(false);
        secure.setReceiversClassesWhiteList(Arrays.asList(
                Object.class,
                String.class,
                GString.class
        ));
        secure.setImportsWhitelist(Arrays.asList(
                "java.lang.String"
        ));
        secure.setStaticImportsWhitelist(Arrays.asList(
                "java.lang.String.*"
        ));
        // secure.setIndirectImportCheckEnabled(true);

        CompilerConfiguration compilerConfiguration = new CompilerConfiguration();
        compilerConfiguration.addCompilationCustomizers(secure);

        Binding binding = new Binding(parameters);
        GroovyShell shell = new GroovyShell(binding, compilerConfiguration);

        Object result = shell.evaluate(expression);
        if (result == null) {
            return null;
        } else if (!(result instanceof String)) {
            throw new ExpressionNotStringException(expression);
        } else {
            return (String) result;
        }
    }
}
