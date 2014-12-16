package net.nemerosa.ontrack.dsl

interface Project {

    int getId()

    String getName()

    String geDescription()

    def call(Closure closure)



}