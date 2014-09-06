package net.nemerosa.ontrack.extension.issues.export;

import java.util.Collection;

public interface IssueExportServiceFactory {

    IssueExportService getIssueExportService(String format) throws IssueExportServiceNotFoundException;

    Collection<IssueExportService> getIssueExportServices();

}