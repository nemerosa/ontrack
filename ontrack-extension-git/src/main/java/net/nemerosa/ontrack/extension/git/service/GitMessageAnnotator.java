package net.nemerosa.ontrack.extension.git.service;


import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.support.MessageAnnotator;

public interface GitMessageAnnotator {

    MessageAnnotator annotator(Branch branch);

}
