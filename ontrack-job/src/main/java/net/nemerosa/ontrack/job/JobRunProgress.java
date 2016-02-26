package net.nemerosa.ontrack.job;

import lombok.Data;

@Data
public class JobRunProgress {

    private final int percentage;
    private final String message;

    public static JobRunProgress message(String pattern, Object... parameters) {
        return new JobRunProgress(-1, String.format(pattern, parameters));
    }

    public String getText() {
        if (percentage >= 0) {
            return String.format("%s (%d %%)", message, percentage);
        } else {
            return message;
        }
    }
}
