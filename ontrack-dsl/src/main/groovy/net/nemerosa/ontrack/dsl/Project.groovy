package net.nemerosa.ontrack.dsl

import net.nemerosa.ontrack.dsl.properties.ProjectProperties

interface Project {

    int getId()

    String getName()

    String geDescription()

    def call(Closure closure)

    /**
     * Creates a branch for the project
     */
    Branch branch(String name)

    /**
     * Creates a branch for the project and configures it
     */
    Branch branch(String name, Closure closure)

    /**
     * Configuration of properties
     */
    ProjectProperties getProperties()
    def properties(Closure closure)
    def property(String type, data)

}