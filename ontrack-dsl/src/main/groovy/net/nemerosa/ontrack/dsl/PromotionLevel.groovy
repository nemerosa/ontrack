package net.nemerosa.ontrack.dsl

import net.nemerosa.ontrack.common.Document

interface PromotionLevel extends ProjectEntity {

    String getProject()

    String getBranch()

    def call(Closure closure)

    /**
     * Sets the image
     */
    def image(Object o)

    /**
     * Gets the image
     */
    Document getImage()

}
