package net.nemerosa.ontrack.dsl

interface Branch {

    int getId()

    String getProject()

    String getName()

    String geDescription()

    // Branch structure

    PromotionLevel promotionLevel(String name, String description)

    Build build(String name, String description)

    // Filters

    List<Build> filter(String filterType, Map<String, ?> filterConfig)

    List<Build> standardFilter(Map<String, ?> filterConfig)

    List<Build> getLastPromotedBuilds()
}