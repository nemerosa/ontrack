package net.nemerosa.ontrack.model.structure;

import java.util.Map;

/**
 * Resolution of expressions.
 */
public interface ExpressionEngine {

    /**
     * Renders an expression in its context
     */
    String render(String template, Map<String, Object> parameters);

    /**
     * Resolves a single expression
     */
    String resolve(String expression, Map<String, Object> parameters);

}
