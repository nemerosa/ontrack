package net.nemerosa.ontrack.model.exceptions;

public class PredefinedPromotionLevelNameAlreadyDefinedException extends DuplicationException {

    public PredefinedPromotionLevelNameAlreadyDefinedException(String name) {
        super("Predefined promotion level name already exists: %s", name);
    }
}
