package net.nemerosa.ontrack.model.support;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ApplicationInfo {

    private final ApplicationInfoType type;
    private final String message;

    public static ApplicationInfo success(String message) {
        return new ApplicationInfo(ApplicationInfoType.SUCCESS, message);
    }

    public static ApplicationInfo info(String message) {
        return new ApplicationInfo(ApplicationInfoType.INFO, message);
    }

    public static ApplicationInfo warning(String message) {
        return new ApplicationInfo(ApplicationInfoType.WARNING, message);
    }

    public static ApplicationInfo error(String message) {
        return new ApplicationInfo(ApplicationInfoType.ERROR, message);
    }

}
