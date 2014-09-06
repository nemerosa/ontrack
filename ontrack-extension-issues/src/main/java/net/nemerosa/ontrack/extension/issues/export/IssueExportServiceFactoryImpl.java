package net.nemerosa.ontrack.extension.issues.export;

import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;

@Service
class IssueExportServiceFactoryImpl implements IssueExportServiceFactory {

    private final Map<String, IssueExportService> issueExportServiceMap;

    @Autowired
    IssueExportServiceFactoryImpl(Collection<IssueExportService> issueExportServices) {
        this.issueExportServiceMap = Maps.uniqueIndex(
                issueExportServices,
                service -> service.getExportFormat().getId()
        );
    }

    @Override
    public IssueExportService getIssueExportService(String format) throws IssueExportServiceNotFoundException {
        IssueExportService issueExportService = issueExportServiceMap.get(format);
        if (issueExportService != null) {
            return issueExportService;
        } else {
            throw new IssueExportServiceNotFoundException(format);
        }
    }

    @Override
    public Collection<IssueExportService> getIssueExportServices() {
        return issueExportServiceMap.values();
    }
}
