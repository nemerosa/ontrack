package net.nemerosa.ontrack.service.support;

import net.nemerosa.ontrack.common.BaseException;
import net.nemerosa.ontrack.model.extension.ExtensionFeature;
import net.nemerosa.ontrack.model.structure.Decoration;
import net.nemerosa.ontrack.model.structure.Decorator;
import net.nemerosa.ontrack.model.structure.ProjectEntity;

import java.util.List;

/**
 * Fake decorator used for the generation of decorations in error.
 */
public class ErrorDecorator implements Decorator<String> {

    @Override
    public List<Decoration<String>> getDecorations(ProjectEntity entity) {
        throw new UnsupportedOperationException("Not a real decorator");
    }

    public Decoration<String> getDecoration(Exception ex) {
        return Decoration.of(
                this,
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

    @Override
    public ExtensionFeature getFeature() {
        return CoreExtensionFeature.INSTANCE;
    }
}
