package net.nemerosa.ontrack.model.structure;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

@Data
public class SignatureRequest {

    private final LocalDateTime time;
    private final String user;

    public static SignatureRequest of(Signature signature) {
        return new SignatureRequest(signature.getTime(), signature.getUser().getName());
    }

    public Signature getSignature(Signature signature) {
        return Signature.of(
                StringUtils.isNotBlank(user) ? user : signature.getUser().getName()
        ).withTime(
                time != null ? time : signature.getTime()
        );
    }
}
