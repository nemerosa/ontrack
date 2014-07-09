package net.nemerosa.ontrack.model.support;

import lombok.Data;

import java.util.List;

@Data
public class ApplicationLogEntries {

    private final List<ApplicationLogEntry> entries;
    private final Page page;
    private final int total;

}
