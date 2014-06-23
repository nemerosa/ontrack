package net.nemerosa.ontrack.extension.svn.model;

import lombok.Data;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Int;

@Data
public class IndexationRange {

    private final long from;
    private final long to;

    public static Form form() {
        return Form.create()
                .with(
                        Int.of("from")
                                .label("From")
                                .min(1)
                                .help("Revision to scan from")
                )
                .with(
                        Int.of("to")
                                .label("To")
                                .min(1)
                                .help("Revision to scan to")
                )
                ;
    }

    public Form asForm() {
        return form()
                .fill("from", from)
                .fill("to", to)
                ;
    }

}
