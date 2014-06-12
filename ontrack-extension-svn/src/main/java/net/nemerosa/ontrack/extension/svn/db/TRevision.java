package net.nemerosa.ontrack.extension.svn.db;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TRevision {

    private final int repository;
    private final long revision;
    private final String author;
    private final LocalDateTime creation;
    private final String message;
    private final String branch;

}
