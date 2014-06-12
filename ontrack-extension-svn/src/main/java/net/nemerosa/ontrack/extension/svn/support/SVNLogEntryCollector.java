package net.nemerosa.ontrack.extension.svn.support;

import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;

import java.util.ArrayList;
import java.util.List;

public class SVNLogEntryCollector implements ISVNLogEntryHandler {

    private final List<SVNLogEntry> entries = new ArrayList<SVNLogEntry>();

    @Override
    public void handleLogEntry(SVNLogEntry entry) throws SVNException {
        entries.add(entry);
    }

    public List<SVNLogEntry> getEntries() {
        return entries;
    }

}
