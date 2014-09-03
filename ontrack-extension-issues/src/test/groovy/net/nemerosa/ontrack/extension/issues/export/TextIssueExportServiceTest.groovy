package net.nemerosa.ontrack.extension.issues.export

import org.junit.Test

class TextIssueExportServiceTest {

    @Test
    void format() {
        TextIssueExportService service = new TextIssueExportService()
        def format = service.exportFormat
        assert format.id == 'text'
        assert format.name == 'Text'
        assert format.type == 'text/plain'
    }

}
