package net.nemerosa.ontrack.extension.svn.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SVNHistory {

    private final List<SVNReference> references;

    public SVNHistory() {
        this(Collections.<SVNReference>emptyList());
    }

    public SVNHistory(SVNReference... references) {
        this(Arrays.asList(references));
    }

    public SVNHistory add(SVNReference reference) {
        List<SVNReference> target = new ArrayList<>(references);
        target.add(reference);
        return new SVNHistory(target);
    }

    public SVNHistory truncateAbove(int index) {
        return new SVNHistory(references.subList(index + 1, references.size()));
    }

    @JsonIgnore
    public long getRevision() {
        return references.get(0).getRevision();
    }
}
