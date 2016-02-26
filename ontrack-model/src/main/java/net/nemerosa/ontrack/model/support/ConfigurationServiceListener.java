package net.nemerosa.ontrack.model.support;

public interface ConfigurationServiceListener<T extends UserPasswordConfiguration> {

    default void onNewConfiguration(T configuration) {
    }

    default void onUpdatedConfiguration(T configuration) {
    }

    default void onDeletedConfiguration(T configuration) {
    }
}
