package net.nemerosa.ontrack.extension.support.configurations;

import net.nemerosa.ontrack.model.support.Configuration;

import java.util.function.Function;

public interface UserPasswordConfiguration<T extends UserPasswordConfiguration<T>> extends Configuration<T> {

    String getUser();

    String getPassword();

    T withPassword(String password);

    T clone(String targetConfigurationName, Function<String, String> replacementFunction);
}
