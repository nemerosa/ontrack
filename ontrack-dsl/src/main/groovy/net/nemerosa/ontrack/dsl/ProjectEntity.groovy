package net.nemerosa.ontrack.dsl

import net.nemerosa.ontrack.dsl.properties.ProjectEntityProperties

interface ProjectEntity {

    int getId()

    String getName()

    String geDescription()

    /**
     * Configuration of properties
     */
    def properties(Closure closure)

    /**
     * Access to the properties
     */
    ProjectEntityProperties getProperties()

    /**
     * Sets a property
     */
    def property(String type, data)

}