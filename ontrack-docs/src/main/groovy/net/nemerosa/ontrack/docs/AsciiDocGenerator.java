package net.nemerosa.ontrack.docs;

import org.apache.commons.lang3.StringUtils;

import java.io.PrintWriter;
import java.util.stream.Collectors;

public class AsciiDocGenerator {

    public void generate(PrintWriter writer, DSLDoc doc) {
        doc.getClasses().values().forEach(
                dslDocClass -> adocClass(writer, dslDocClass)
        );
    }

    private void adocClass(PrintWriter writer, DSLDocClass docClass) {
        writer.format("[[dsl-%s]]%n", docClass.getId());
        writer.format("==== %s%n", docClass.getName());
        // Description
        if (StringUtils.isNotBlank(docClass.getDescription())) {
            writer.format("%n%s%n", docClass.getDescription());
        }
        if (StringUtils.isNotBlank(docClass.getLongDescription())) {
            writer.format("%n%s%n", docClass.getLongDescription());
        }
        // See also section
        if (!docClass.getReferences().isEmpty()) {
            writer.format(
                    "%nSee also: %s%n",
                    docClass.getReferences().stream()
                            .map(AsciiDocGenerator::getRefLink)
                            .collect(Collectors.joining(", "))
            );
        }
        // Sample
        adocSample(writer, docClass.getSample());
        // Methods
        docClass.getMethods().forEach(
                dslDocMethod -> adocMethod(writer, docClass, dslDocMethod)
        );
        // Separator
        writer.println();
    }

    private void adocSample(PrintWriter writer, String sample) {
        if (StringUtils.isNotBlank(sample)) {
            writer.format("%n[source,groovy]%n");
            writer.format("----%n");
            writer.println(sample);
            writer.format("----%n");
        }
    }

    private void adocMethod(PrintWriter writer, DSLDocClass docClass, DSLDocMethod docMethod) {
        writer.format("%n[[dsl-%s-%s]]%n", docClass.getId(), docMethod.getId());
        writer.format("===== %s%n", docMethod.getSignature());
        if (StringUtils.isNotBlank(docMethod.getDescription())) {
            writer.format("%n%s%n", docMethod.getDescription());
        }
        if (StringUtils.isNotBlank(docMethod.getLongDescription())) {
            writer.format("%n%s%n", docMethod.getLongDescription());
        }
        adocSample(writer, docMethod.getSample());
        // References
        if (!docMethod.getReferences().isEmpty()) {
            writer.format(
                    "%nSee: %s%n",
                    docMethod.getReferences().stream()
                            .map(AsciiDocGenerator::getRefLink)
                            .collect(Collectors.joining(", "))
            );
        }
    }

    private static String getRefLink(DSLDocClass ref) {
        return String.format("<<dsl-%s,%s>>", ref.getId(), ref.getName());
    }

}
