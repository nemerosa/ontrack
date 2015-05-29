package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.common.Document;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.PredefinedValidationStamp;

import java.util.List;
import java.util.Optional;

public interface PredefinedValidationStampRepository {

    List<PredefinedValidationStamp> getPredefinedValidationStamps();

    ID newPredefinedValidationStamp(PredefinedValidationStamp stamp);

    PredefinedValidationStamp getPredefinedValidationStamp(ID id);

    Optional<PredefinedValidationStamp> findPredefinedValidationStampByName(String name);

    Document getPredefinedValidationStampImage(ID id);

    void savePredefinedValidationStamp(PredefinedValidationStamp stamp);
}
