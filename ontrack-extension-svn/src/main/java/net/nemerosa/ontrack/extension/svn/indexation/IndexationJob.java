package net.nemerosa.ontrack.extension.svn.indexation;


import net.nemerosa.ontrack.extension.svn.db.SVNRepository;

public interface IndexationJob {

    boolean isRunning();

    long getMin();

    long getMax();

    long getCurrent();

    /**
     * Returns the job progression in percentage (0..100)
     */
    int getProgress();

    /**
     * Returns the associated SVN configuration
     */
    SVNRepository getRepository();

}
