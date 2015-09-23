package net.nemerosa.ontrack.model.extension;

import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.PropertyType;
import net.nemerosa.ontrack.model.structure.ValidationStamp;

import java.util.Optional;

public interface ValidationStampPropertyType<T> extends PropertyType<T> {

    Optional<ValidationStamp> getOrCreateValidationStamp(T value, Branch branch, String validationStampName);

}
