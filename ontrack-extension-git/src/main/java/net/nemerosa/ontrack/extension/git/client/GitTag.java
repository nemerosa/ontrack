package net.nemerosa.ontrack.extension.git.client;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GitTag implements Comparable<GitTag> {

    private final String name;
    private final LocalDateTime time;

    @Override
    public int compareTo(GitTag o) {
        return this.time.compareTo(o.time);
    }
}
