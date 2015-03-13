package net.nemerosa.ontrack.model.structure;

import lombok.Data;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Text;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

@Data
public class SignatureRequest {

    private final LocalDateTime time;
    private final String user;

    public static SignatureRequest of(Signature signature) {
        return new SignatureRequest(signature.getTime(), signature.getUser().getName());
    }

    public static Form form() {
        return Form.create()
                .with(Text.of("user").label("User name").optional())
                .with(Text.of("time").label("Date/time").optional())
                ;
    }

    public Form asForm() {
        return form()
                .fill("user", user)
                .fill("time", time)
                ;
    }

    public Signature getSignature(Signature signature) {
        return Signature.of(
                StringUtils.isNotBlank(user) ? user : signature.getUser().getName()
        ).withTime(
                time != null ? time : signature.getTime()
        );
    }
}
