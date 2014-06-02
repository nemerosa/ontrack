package net.nemerosa.ontrack.model.settings;

import lombok.Data;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.YesNo;

@Data
public class SecuritySettings {

    private final boolean grantProjectViewToAll;

    public static SecuritySettings of() {
        return new SecuritySettings(false);
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
