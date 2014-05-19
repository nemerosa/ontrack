package net.nemerosa.ontrack.model.exceptions;

public class PromotionLevelNameAlreadyDefinedException extends DuplicationException {

    public PromotionLevelNameAlreadyDefinedException(String name) {
        super("Promotion level name already exists: %s", name);
    }
}
