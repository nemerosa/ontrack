package net.nemerosa.ontrack.extension.git.client;

import lombok.Data;
import net.nemerosa.ontrack.extension.git.client.plot.GPlot;

import java.util.List;

@Data
public class GitLog {

    private final GPlot plot;
    private final List<GitCommit> commits;

}
