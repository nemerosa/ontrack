package net.nemerosa.ontrack.acceptance

import org.junit.runner.JUnitCore

class Start {

    static void main(String... args) {
        println "Starting acceptance tests."
        println "Options: ${args}"

        JUnitCore junit = new JUnitCore()
        // TODO Filtering on tests
        junit.run(
                ACCBrowserBasic,
//                ACCSearch,
//                ACCStructure
        )
    }

}
