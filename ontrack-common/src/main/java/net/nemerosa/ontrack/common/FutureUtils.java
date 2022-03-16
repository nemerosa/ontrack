package net.nemerosa.ontrack.common;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.*;

public abstract class FutureUtils {

    public static <T> T wait(String message, Optional<Future<? extends T>> task) {
        if (task.isPresent()) {
            return wait(message, task.get());
        } else {
            throw new TaskNotScheduledException(message);
        }
    }

    public static <T> T wait(String message, @NotNull Future<T> task) {
        return wait(message, task, 300);
    }

    public static <T> T wait(String message, @NotNull Future<T> task, long seconds) {
        try {
            return task.get(seconds, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new TaskInterruptedException(message);
        } catch (CancellationException e) {
            throw new TaskCancelledException(message);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            } else {
                throw new TaskExecutionException(message, cause);
            }
        } catch (TimeoutException e) {
            throw new TaskTimeoutException(message, seconds);
        }
    }
}
