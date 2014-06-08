package net.nemerosa.ontrack.extension.jenkins.client;

import net.nemerosa.ontrack.model.exceptions.InputException;

public class JenkinsClientNotFoundException extends InputException {
    public JenkinsClientNotFoundException(String url) {
        super(url);
    }
}
