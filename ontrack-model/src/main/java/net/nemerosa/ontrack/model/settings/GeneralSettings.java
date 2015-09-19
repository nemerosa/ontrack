package net.nemerosa.ontrack.model.settings;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Wither;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Int;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class GeneralSettings {

    @Wither
    private final int disablingDuration;
    @Wither
    private final int deletingDuration;

    public static GeneralSettings of() {
        return new GeneralSettings(0, 0);
    }

    public Form form() {
        return Form.create()
                .with(
                        Int.of("disablingDuration")
                                .label("Disabling branches after N (days)")
                                .min(0)
                                .help("Number of days of inactivity after a branch is disabled. 0 means that " +
                                        "the branch won't ever be disabled automatically.")
                                .value(getDisablingDuration())
                )
                .with(
                        Int.of("deletingDuration")
                                .label("Deleting branches after N (days) more")
                                .min(0)
                                .help("Number of days of inactivity after a branch is deleted, after it has been" +
                                        "disabled automatically. 0 means that " +
                                        "the branch won't ever be deleted automatically.")
                                .value(getDisablingDuration())
                )
                ;
    }
}
