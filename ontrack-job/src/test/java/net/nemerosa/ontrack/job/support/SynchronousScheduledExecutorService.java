package net.nemerosa.ontrack.job.support;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;


/**
 * A {@link ScheduledExecutorService} that executes commands on the thread that calls
 * {@link #runNextPendingCommand() runNextPendingCommand}, {@link #runUntilIdle() runUntilIdle} or
 * {@link #tick(long, TimeUnit) tick}.
 */
public class SynchronousScheduledExecutorService implements ScheduledExecutorService {

    private final DeltaQueue<ScheduledTask<?>> deltaQueue = new DeltaQueue<ScheduledTask<?>>();

    /**
     * Runs time forwards by a given duration, executing any commands scheduled for
     * execution during that time period, and any background tasks spawned by the
     * scheduled tasks.  Therefore, when a call to tick returns, the executor
     * will be idle.
     *
     * @param duration Number of units to tock
     * @param timeUnit Unit used for the ticking
     */
    public void tick(long duration, TimeUnit timeUnit) {
        long remaining = toTicks(duration, timeUnit);

        do {
            remaining = deltaQueue.tick(remaining);
            runUntilIdle();

        } while (deltaQueue.isNotEmpty() && remaining > 0);
    }

    /**
     * Runs all commands scheduled to be executed immediately but does
     * not tick time forward.
     */
    public void runUntilIdle() {
        while (!isIdle()) {
            runNextPendingCommand();
        }
    }

    /**
     * Runs the next command scheduled to be executed immediately.
     */
    public void runNextPendingCommand() {
        ScheduledTask<?> scheduledTask = deltaQueue.pop();

        scheduledTask.run();

        if (!scheduledTask.isCancelled() && scheduledTask.repeats()) {
            deltaQueue.add(scheduledTask.repeatDelay, scheduledTask);
        }
    }

    /**
     * Reports whether scheduler is "idle": has no commands pending immediate execution.
     *
     * @return true if there are no commands pending immediate execution,
     * false if there are commands pending immediate execution.
     */
    public boolean isIdle() {
        return deltaQueue.isEmpty() || deltaQueue.delay() > 0;
    }

    public void execute(Runnable command) {
        schedule(command, 0, TimeUnit.SECONDS);
    }

    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        ScheduledTask<Void> task = new ScheduledTask<>(command);
        deltaQueue.add(toTicks(delay, unit), task);
        return task;
    }

    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        ScheduledTask<V> task = new ScheduledTask<>(callable);
        deltaQueue.add(toTicks(delay, unit), task);
        return task;
    }

    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        return scheduleWithFixedDelay(command, initialDelay, period, unit);
    }

    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        ScheduledTask<Object> task = new ScheduledTask<>(toTicks(delay, unit), command);
        deltaQueue.add(toTicks(initialDelay, unit), task);
        return task;
    }

    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        throw blockingOperationsNotSupported();
    }

    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        throw blockingOperationsNotSupported();
    }

    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        throw blockingOperationsNotSupported();
    }

    public <T> T invokeAny(Collection<? extends Callable<T>> tasks)
            throws InterruptedException, ExecutionException {
        throw blockingOperationsNotSupported();
    }

    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        throw blockingOperationsNotSupported();
    }

    public boolean isShutdown() {
        throw shutdownNotSupported();
    }

    public boolean isTerminated() {
        throw shutdownNotSupported();
    }

    public void shutdown() {
        throw shutdownNotSupported();
    }

    public List<Runnable> shutdownNow() {
        throw shutdownNotSupported();
    }

    public <T> Future<T> submit(Callable<T> callable) {
        return schedule(callable, 0, TimeUnit.SECONDS);
    }

    public Future<?> submit(Runnable command) {
        return submit(command, null);
    }

    public <T> Future<T> submit(Runnable command, T result) {
        return submit(new CallableRunnableAdapter<>(command, result));
    }

    private final class CallableRunnableAdapter<T> implements Callable<T> {
        private final Runnable runnable;
        private final T result;

        public CallableRunnableAdapter(Runnable runnable, T result) {
            this.runnable = runnable;
            this.result = result;
        }

        @Override
        public String toString() {
            return runnable.toString();
        }

        public T call() throws Exception {
            runnable.run();
            return result;
        }
    }

    private final class ScheduledTask<T> implements ScheduledFuture<T>, Runnable {
        public final long repeatDelay;
        public final Callable<T> command;
        private boolean isCancelled = false;
        private boolean isDone = false;
        private T futureResult;
        private Exception failure = null;

        public ScheduledTask(Callable<T> command) {
            this.repeatDelay = -1;
            this.command = command;
        }

        public ScheduledTask(Runnable command) {
            this(-1, command);
        }

        public ScheduledTask(long repeatDelay, Runnable command) {
            this.repeatDelay = repeatDelay;
            this.command = new CallableRunnableAdapter<>(command, null);
        }

        @Override
        public String toString() {
            return command.toString() + " repeatDelay=" + repeatDelay;
        }

        public boolean repeats() {
            return repeatDelay >= 0;
        }

        public long getDelay(TimeUnit unit) {
            return unit.convert(deltaQueue.delay(this), TimeUnit.MILLISECONDS);
        }

        public int compareTo(Delayed o) {
            throw new UnsupportedOperationException("not supported");
        }

        public boolean cancel(boolean mayInterruptIfRunning) {
            isCancelled = true;
            return deltaQueue.remove(this);
        }

        public T get() throws InterruptedException, ExecutionException {
            if (!isDone) {
                throw blockingOperationsNotSupported();
            }

            if (failure != null) {
                throw new ExecutionException(failure);
            }

            return futureResult;
        }

        public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return get();
        }

        public boolean isCancelled() {
            return isCancelled;
        }

        public boolean isDone() {
            return isDone;
        }

        public void run() {
            try {
                futureResult = command.call();
            } catch (Exception e) {
                failure = e;
            }
            isDone = true;
        }
    }

    private long toTicks(long duration, TimeUnit timeUnit) {
        return TimeUnit.MILLISECONDS.convert(duration, timeUnit);
    }

    private UnsupportedSynchronousOperationException blockingOperationsNotSupported() {
        return new UnsupportedSynchronousOperationException("cannot perform blocking wait on a task scheduled on a " + SynchronousScheduledExecutorService.class.getName());
    }

    private UnsupportedOperationException shutdownNotSupported() {
        return new UnsupportedOperationException("shutdown not supported");
    }
}