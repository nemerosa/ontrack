package net.nemerosa.ontrack.extension.git.client.plot;

public abstract class AbstractGItem implements GItem {

    @Override
    public String getType() {
        return getClass().getSimpleName().substring(1).toLowerCase();
    }
}
