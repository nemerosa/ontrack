package net.nemerosa.ontrack.model.structure;

import java.util.Map;

/**
 * Resolution of expressions.
 */
public interface ExpressionEngine {

    /**
     * Renders an expression in its context
     */
    String render(String template, Map<String, ?> parameters);

    /**
     * Resolves a single expression
     */
    String resolve(String expression, Map<String, ?> parameters);

}
