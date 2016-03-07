package net.nemerosa.ontrack.model.buildfilter;

import java.time.LocalDate;

public interface StandardFilterDataBuilder {

    StandardFilterDataBuilder withSincePromotionLevel(String sincePromotionLevel);

    StandardFilterDataBuilder withWithPromotionLevel(String withPromotionLevel);

    StandardFilterDataBuilder withAfterDate(LocalDate afterDate);

    StandardFilterDataBuilder withBeforeDate(LocalDate beforeDate);

    StandardFilterDataBuilder withSinceValidationStamp(String sinceValidationStamp);

    StandardFilterDataBuilder withSinceValidationStampStatus(String sinceValidationStampStatus);

    StandardFilterDataBuilder withWithValidationStamp(String withValidationStamp);

    StandardFilterDataBuilder withWithValidationStampStatus(String withValidationStampStatus);

    StandardFilterDataBuilder withWithProperty(String withProperty);

    StandardFilterDataBuilder withWithPropertyValue(String withPropertyValue);

    StandardFilterDataBuilder withSinceProperty(String sinceProperty);

    StandardFilterDataBuilder withSincePropertyValue(String sincePropertyValue);
}
