package net.nemerosa.ontrack.extension.git.client.impl;

import lombok.Data;

@Data
public class GitRepositoryKey {

    private final String remote;
    private final String branch;

}
