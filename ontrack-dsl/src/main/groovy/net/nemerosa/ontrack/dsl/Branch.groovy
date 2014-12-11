package net.nemerosa.ontrack.dsl

interface Branch {

    String getProject()

    String getName()

    String geDescription()

    List<Build> filter(String filterType, Map<String, ?> filterConfig)

}