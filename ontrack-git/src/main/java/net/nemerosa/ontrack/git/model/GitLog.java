package net.nemerosa.ontrack.git.model;

import lombok.Data;
import net.nemerosa.ontrack.git.model.plot.GPlot;
import net.nemerosa.ontrack.git.model.GitCommit;

import java.util.List;

@Data
public class GitLog {

    private final GPlot plot;
    private final List<GitCommit> commits;

}
