package net.nemerosa.ontrack.repository;

import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.Build;
import net.nemerosa.ontrack.model.structure.PropertyType;
import net.nemerosa.ontrack.model.structure.StandardBuildFilterData;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Standard filter query.
 */
public interface CoreBuildFilterRepository {

    List<Build> standardFilter(Branch branch, StandardBuildFilterData data, Function<String, PropertyType<?>> propertyTypeAccessor);

    List<Build> nameFilter(Branch branch, String fromBuild, String toBuild, String withPromotionLevel, int count);

    Optional<Build> lastBuild(Branch branch, String sinceBuild, String withPromotionLevel);

    List<Build> between(Branch branch, String from, String to);
}
