package net.nemerosa.ontrack.dsl

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

}