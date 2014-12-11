package net.nemerosa.ontrack.dsl

interface Build {

    int getId()

    String getProject()

    String getBranch()

    String getName()

    String geDescription()

    Build promote(PromotionLevel promotionLevel)

}
