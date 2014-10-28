package net.nemerosa.ontrack.acceptance.boot

import groovy.xml.MarkupBuilder
import org.junit.runner.Description
import org.junit.runner.Result
import org.junit.runner.notification.Failure
import org.junit.runner.notification.RunListener

class XMLRunListener extends RunListener {

    private long runStart;
    private long runEnd;

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
            end - start
        }

        void end() {
            end = System.currentTimeMillis()
        }

    }

    @Override
    void testRunStarted(Description description) throws Exception {
        runStart = System.currentTimeMillis()
    }

    @Override
    void testRunFinished(Result result) throws Exception {
        runEnd = System.currentTimeMillis()
    }

    @Override
    void testStarted(Description description) throws Exception {
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
        runs[description].ignored = true
    }

    void render(File file) {
        // Output
        def writer = new FileWriter(file)
        def xml = new MarkupBuilder(writer)
        xml.testsuite(
                tests: runs.size(),
                skipped: runs.values().count { it.ignored },
                failures: runs.values().count { it.failure },
                errors: runs.values().count { it.error },
                time: (runEnd - runStart) / 1000
        ) {
            runs.values().each { run ->
                testcase(
                        name: run.description.methodName,
                        classname: run.description.className,
                        time: (run.time) / 1000
                )
            }
        }
    }

}
