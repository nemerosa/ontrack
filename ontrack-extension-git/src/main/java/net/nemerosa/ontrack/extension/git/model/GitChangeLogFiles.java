package net.nemerosa.ontrack.extension.git.model;

import lombok.Data;

import java.util.List;

@Data
public class GitChangeLogFiles {

    private final List<GitChangeLogFile> list;

}
