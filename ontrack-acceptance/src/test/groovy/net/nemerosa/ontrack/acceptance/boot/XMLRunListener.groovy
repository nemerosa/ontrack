package net.nemerosa.ontrack.acceptance.boot

import groovy.xml.MarkupBuilder
import org.apache.commons.lang3.exception.ExceptionUtils
import org.junit.runner.Description
import org.junit.runner.Result
import org.junit.runner.notification.Failure
import org.junit.runner.notification.RunListener

class XMLRunListener extends RunListener {

    private final PrintStream stream

    private Map<Description, TestRun> runs = [:]

    private static class TestRun {

        private final Description description
        private long start
        private long end
        Failure error
        Failure failure
        boolean ignored

        TestRun(Description description) {
            this.description = description
            this.start = System.currentTimeMillis()
        }

        long getTime() {
            ignored ? 0 : (end - start)
        }

        void end() {
            end = System.currentTimeMillis()
        }

    }

    XMLRunListener(PrintStream stream) {
        this.stream = stream
    }

    protected void trace(String message) {
        stream.println message
    }

    @Override
    void testRunStarted(Description description) throws Exception {
        trace "Starting tests in ${description.className}..."
    }

    @Override
    void testRunFinished(Result result) throws Exception {
        if (result.wasSuccessful()) {
            trace ''
            trace "Tests OK (${result.runCount} test${result.runCount > 1 ? 's' : ''})"

        } else {
            trace ''
            trace 'FAILED!'
            trace "Tests run: ${result.runCount}, Failures: ${result.failureCount}"
        }
    }

    @Override
    void testStarted(Description description) throws Exception {
        trace "Running test: ${description.className}: ${description.methodName}"
        runs.put description, new TestRun(description)
    }

    @Override
    void testFinished(Description description) throws Exception {
        runs[description].end()
    }

    @Override
    void testFailure(Failure failure) throws Exception {
        runs[failure.description].error = failure
    }

    @Override
    void testAssumptionFailure(Failure failure) {
        runs[failure.description].failure = failure
    }

    @Override
    void testIgnored(Description description) throws Exception {
        trace "(*) Ignoring test: ${description.className}: ${description.methodName}"
        def run = new TestRun(description)
        run.ignored = true
        runs.put description, run
    }

    void render(File file) {
        // Output
        file.parentFile.mkdirs()
        def writer = new FileWriter(file)
        def xml = new MarkupBuilder(writer)
        xml.testsuite(
                tests: runs.size(),
                skipped: runs.values().count { it.ignored },
                failures: runs.values().count { it.failure },
                errors: runs.values().count { it.error },
                time: (runs.values().sum { it.time }) / 1000
        ) {
            runs.values().each { run ->
                testcase(
                        name: run.description.methodName,
                        classname: run.description.className,
                        time: (run.time) / 1000
                ) {
                    if (run.ignored) {
                        skipped()
                    } else if (run.error) {
                        failure(
                                message: run.error.message,
                                type: run.error.class.name,
                                errorMessage(run.error)
                        )
                    }
                }
            }
        }
    }

    protected static String errorMessage(Failure failure) {
        ExceptionUtils.getStackTrace(failure.exception)
    }

}
