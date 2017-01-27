package net.nemerosa.ontrack.job.support;

/**
 * Embeds another task into monitoring activities.
 */
public class MonitoredRun implements Runnable {

    private final Runnable embedded;
    private final MonitoredRunListener runListener;

    public MonitoredRun(Runnable embedded, MonitoredRunListener runListener) {
        this.embedded = embedded;
        this.runListener = runListener;
    }

    @Override
    public void run() {
        try {
            runListener.onStart();
            // Runs the job
            long _start = System.currentTimeMillis();
            embedded.run();
            // No error, counting time
            long _end = System.currentTimeMillis();
            runListener.onSuccess(_end - _start);
        } catch (Exception ex) {
            runListener.onFailure(ex);
            // Rethrows the error
            throw ex;
        } finally {
            runListener.onCompletion();
        }
    }
}
