package net.nemerosa.ontrack.dsl

import net.nemerosa.ontrack.dsl.properties.ProjectEntityProperties

interface ProjectEntity {

    int getId()

    String getName()

    String getDescription()

    /**
     * Configuration of properties
     */
    def config(Closure closure)

    /**
     * Access to the properties
     */
    ProjectEntityProperties getConfig()

    /**
     * Sets a property
     */
    def property(String type, data)

    /**
     * Gets a property
     */
    def getProperty(String type, boolean required)

    /**
     * Gets a required property
     */
    def property(String type)

}