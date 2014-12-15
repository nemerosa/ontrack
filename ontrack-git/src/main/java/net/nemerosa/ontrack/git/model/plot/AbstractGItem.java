package net.nemerosa.ontrack.git.model.plot;

public abstract class AbstractGItem implements GItem {

    @Override
    public String getType() {
        return getClass().getSimpleName().substring(1).toLowerCase();
    }
}
