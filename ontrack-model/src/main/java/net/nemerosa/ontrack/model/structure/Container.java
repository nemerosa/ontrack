package net.nemerosa.ontrack.model.structure;

import java.util.Optional;

public interface Container<T> {

    Optional<T> first();

}
