package net.nemerosa.ontrack.model.support;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Function;

public interface UserPasswordConfiguration<T extends UserPasswordConfiguration<T>> extends Configuration<T> {

    /**
     * Name of the configuration
     */
    String getName();

    @Nullable
    String getUser();

    @Nullable
    String getPassword();

    T withPassword(@Nullable String password);

    T clone(String targetConfigurationName, Function<String, String> replacementFunction);

    @JsonIgnore
    default Optional<UserPassword> getCredentials() {
        String user = getUser();
        if (StringUtils.isNotBlank(user)) {
            return Optional.of(new UserPassword(user, getPassword()));
        } else {
            return Optional.empty();
        }
    }
}
