package net.nemerosa.ontrack.service.support;

import net.nemerosa.ontrack.common.BaseException;
import net.nemerosa.ontrack.model.structure.Decoration;
import net.nemerosa.ontrack.model.structure.Decorator;
import net.nemerosa.ontrack.model.structure.ProjectEntity;

import java.util.List;

/**
 * Fake decorator used for the generation of decorations in error.
 */
public class ErrorDecorator implements Decorator {

    @Override
    public List<Decoration> getDecorations(ProjectEntity entity) {
        throw new UnsupportedOperationException("Not a real decorator");
    }

    public Decoration getDecoration(Exception ex) {
        return Decoration.of(
                ErrorDecorator.class,
                "error",
                getErrorMessage(ex)
        );
    }

    protected String getErrorMessage(Exception ex) {
        if (ex instanceof BaseException) {
            return ex.getMessage();
        } else {
            return "Problem while getting decoration";
        }
    }
}
