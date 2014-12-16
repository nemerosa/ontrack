package net.nemerosa.ontrack.service;

import groovy.lang.Binding;
import groovy.lang.GString;
import groovy.lang.GroovyShell;
import groovy.lang.MissingPropertyException;
import net.nemerosa.ontrack.model.exceptions.ExpressionCompilationException;
import net.nemerosa.ontrack.model.exceptions.ExpressionNotStringException;
import net.nemerosa.ontrack.model.structure.ExpressionEngine;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.MultipleCompilationErrorsException;
import org.codehaus.groovy.control.customizers.SecureASTCustomizer;
import org.codehaus.groovy.control.messages.ExceptionMessage;
import org.codehaus.groovy.control.messages.Message;
import org.kohsuke.groovy.sandbox.GroovyValueFilter;
import org.kohsuke.groovy.sandbox.SandboxTransformer;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Default implementation for an expression engine, based on a Grovy sandbox
 */
@Component
public class ExpressionEngineImpl implements ExpressionEngine {

    /**
     * Any <code>${...} expression</code>. Any curved bracket in the expression must be escaped
     * using <code>$\</code>.
     */
    public static final Pattern PATTERN = Pattern.compile("\\$\\{([^\\}]+)\\}");

    public static final String ESCAPE_RIGHT = "@@@@@";
    public static final String ESCAPE_LEFT = "&&&&&";

    @Override
    public String render(String template, Map<String, ?> parameters) {
        // Null handling
        if (template == null) {
            return null;
        }
        // Escaping the curved brackets
        String escapedTemplate = template.replace("\\}", ESCAPE_RIGHT).replace("\\{", ESCAPE_LEFT);
        // Pattern matching
        Matcher matcher = PATTERN.matcher(escapedTemplate);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String expression = matcher.group(1).replace(ESCAPE_RIGHT, "}").replace(ESCAPE_LEFT, "{");
            String resolution = resolve(expression, parameters);
            matcher.appendReplacement(buffer, resolution);
        }
        matcher.appendTail(buffer);
        // OK
        return buffer.toString();
    }

    public String resolve(final String expression, Map<String, ?> parameters) {

        SandboxTransformer sandboxTransformer = new SandboxTransformer();

        SecureASTCustomizer secure = new SecureASTCustomizer();
        secure.setClosuresAllowed(false);
        secure.setMethodDefinitionAllowed(false);

        CompilerConfiguration compilerConfiguration = new CompilerConfiguration();
        compilerConfiguration.addCompilationCustomizers(sandboxTransformer, secure);

        Binding binding = new Binding(parameters);
        GroovyShell shell = new GroovyShell(binding, compilerConfiguration);

        // Sandbox registration (thread level)
        GroovyValueFilter sandboxFilter = new GroovyValueFilter() {
            @Override
            public Object filter(Object o) {
                if (o == null
                        || o instanceof String
                        || o instanceof GString
                        || o.getClass().getName().equals("Script1")
                        ) {
                    return o;
                } else if (o instanceof Class) {
                    throw new ExpressionCompilationException(
                            expression,
                            String.format(
                                    "%n- %s class cannot be accessed.",
                                    ((Class) o).getName()
                            )
                    );
                } else {
                    throw new ExpressionCompilationException(
                            expression,
                            String.format(
                                    "%n- %s class cannot be accessed.",
                                    o.getClass().getName()
                            )
                    );
                }
            }
        };
        try {
            sandboxFilter.register();
            Object result = shell.evaluate(expression);
            if (result == null) {
                return null;
            } else if (!(result instanceof String)) {
                throw new ExpressionNotStringException(expression);
            } else {
                return (String) result;
            }
        } catch (MissingPropertyException e) {
            throw new ExpressionCompilationException(expression, "No such property: " + e.getProperty());
        } catch (MultipleCompilationErrorsException e) {
            StringWriter s = new StringWriter();
            PrintWriter p = new PrintWriter(s);
            @SuppressWarnings("unchecked")
            List<Message> errors = e.getErrorCollector().getErrors();
            errors.forEach((Message message) -> writeErrorMessage(p, message));
            throw new ExpressionCompilationException(expression, s.toString());
        } finally {
            sandboxFilter.unregister();
        }
    }

    private void writeErrorMessage(PrintWriter p, Message message) {
        if (message instanceof ExceptionMessage) {
            // Just writes the cause
            //noinspection ThrowableResultOfMethodCallIgnored
            p.format("%n- %s", ((ExceptionMessage) message).getCause().getMessage());
        }
    }
}
