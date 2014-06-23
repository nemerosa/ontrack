package net.nemerosa.ontrack.extension.svn.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SVNHistory {

    private final List<SVNReference> references;

    public SVNHistory() {
        this(Collections.<SVNReference>emptyList());
    }

    public SVNHistory(SVNReference reference) {
        this(Collections.singletonList(reference));
    }

    public SVNHistory add(SVNReference reference) {
        List<SVNReference> target = new ArrayList<>(references);
        target.add(reference);
        return new SVNHistory(target);
    }

    public SVNHistory truncateAbove(int index) {
        return new SVNHistory(references.subList(index + 1, references.size()));
    }
}
