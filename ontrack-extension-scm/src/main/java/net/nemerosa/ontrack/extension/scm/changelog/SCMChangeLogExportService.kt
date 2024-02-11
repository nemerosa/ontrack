package net.nemerosa.ontrack.extension.scm.changelog

interface SCMChangeLogExportService {

    fun export(changeLog: SCMChangeLog?, input: SCMChangeLogExportInput?): String

}