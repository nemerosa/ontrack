package net.nemerosa.ontrack.dsl

interface Build extends ProjectEntity {

    String getProject()

    String getBranch()

    Build promote(String promotion)

    Build validate(String validationStamp, String validationStampStatus)

}
