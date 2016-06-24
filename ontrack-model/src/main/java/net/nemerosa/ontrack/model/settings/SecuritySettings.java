package net.nemerosa.ontrack.model.settings;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Wither;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.YesNo;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SecuritySettings {

    @Wither
    private final boolean grantProjectViewToAll;

    /**
     * By default, grants view accesses to everybody.
     */
    public static SecuritySettings of() {
        return new SecuritySettings(true);
    }

    public Form form() {
        return Form.create()
                .with(
                        YesNo.of("grantProjectViewToAll")
                                .label("Grants project view to all")
                                .help("Unless disabled at project level, this would enable any user (even anonymous) " +
                                        "to view the content of all projects.")
                                .value(isGrantProjectViewToAll())
                );
    }
}
