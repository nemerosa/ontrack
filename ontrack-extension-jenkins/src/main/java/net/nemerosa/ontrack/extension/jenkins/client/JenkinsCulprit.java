package net.nemerosa.ontrack.extension.jenkins.client;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class JenkinsCulprit {

    private final String id;
    private final String fullName;
    private final boolean claim;
    private final String imageUrl;

    public JenkinsCulprit(JenkinsUser user) {
        this(user.getId(), user.getFullName(), false, user.getImageUrl());
    }

    public JenkinsCulprit claim() {
        return new JenkinsCulprit(
                id,
                fullName,
                true,
                imageUrl
        );
    }
}
