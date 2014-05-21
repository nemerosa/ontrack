package net.nemerosa.ontrack.extension.jenkins.model;

import lombok.Data;

import java.util.Collection;

@Data
public class JenkinsSettings {

    private final Collection<JenkinsConfiguration> configurations;

}
