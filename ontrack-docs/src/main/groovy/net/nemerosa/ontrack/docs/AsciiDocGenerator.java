package net.nemerosa.ontrack.docs;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.stream.Collectors;

public class AsciiDocGenerator {

    public void generate(File dir, DSLDoc doc) throws IOException, ClassNotFoundException {
        // Index file
        File indexFile = new File(dir, "dsl-index.adoc");
        System.out.format("[doc] Writing index %s%n", indexFile.getName());
        try (PrintWriter writer = new PrintWriter(indexFile)) {
            adocIndex(writer, doc);
        }
        // One file per class
        doc.getClasses().values()
                .stream()
                .filter(dslDocClass -> !dslDocClass.isPropertyClass())
                .forEach(
                        dslDocClass -> adocClass(dir, dslDocClass)
                );
    }

    private void adocClass(File dir, DSLDocClass docClass) {
        File clsFile = new File(dir, String.format("dsl-%s.adoc", docClass.getId()));
        System.out.format("[doc] Writing class %s in %s%n", docClass.getName(), clsFile.getName());
        try (PrintWriter writer = new PrintWriter(clsFile)) {
            adocClass(writer, docClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void adocIndex(PrintWriter writer, DSLDoc doc) throws IOException, ClassNotFoundException {
        // Class summary
        writer.format("%n|===%n");
        writer.format("3+| Class summary%n");
        writer.format("h| Class | |%n%n");
        // Ontrack
        writer.format("| <<dsl-ontrack,Ontrack>> | - | <<dsl-ontrack-methods,Methods>> %n%n");
        // Other classes
        doc.getClasses().values()
                .stream()
                .filter(c -> !StringUtils.equals("ontrack", c.getId()))
                .filter(c -> !c.isPropertyClass())
                .sorted(Comparator.comparing(DSLDocClass::getName))
                .forEach(docClass -> writer.format("| <<dsl-%s,%s>> | %s | %s%n%n",
                        docClass.getId(),
                        docClass.getName(),
                        docClass.getProperties().get() != null ? String.format("<<dsl-%s-properties,Properties>>", docClass.getId()) : "",
                        docClass.getMethods().isEmpty() ? "" : String.format("<<dsl-%s-methods,Methods>>", docClass.getId())
                ));

        writer.format("%n|===%n");
        // Only Ontrack at the beginning
        writer.format("include::dsl-ontrack.adoc[]%n%n");
        // All classes but Ontrack
        doc.getClasses().values()
                .stream()
                .filter(c -> !StringUtils.equals("ontrack", c.getId()))
                .filter(c -> !c.isPropertyClass())
                .sorted(Comparator.comparing(DSLDocClass::getName))
                .forEach(docClass ->
                        writer.format("include::dsl-%s.adoc[]%n%n", docClass.getId())
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
        // Properties
        DSLDocClass propertyClass = docClass.getProperties().get();
        if (propertyClass != null) {
            // Link to methods
            writer.format("%nGo to the <<dsl-%s-methods,methods>>%n", docClass.getId());
            // Properties section
            adocPropertyClass(writer, docClass, propertyClass);
        }
        // Tables of methods
        if (!docClass.getMethods().isEmpty()) {
            writer.format("[[dsl-%s-methods]]%n", docClass.getId());
            writer.format("|===%n");
            writer.format("2+h| Method summary%n");
            writer.format("| Method | Description%n%n");
            docClass.getMethods()
                    .stream()
                    .sorted(Comparator.comparing(DSLDocMethod::getName))
                    .forEach(dslDocMethod ->
                            writer.format(
                                    "| <<dsl-%s-%s,`%s`>> | `%s`%n%n%s%n%n",
                                    docClass.getId(),
                                    dslDocMethod.getId(),
                                    dslDocMethod.getName(),
                                    dslDocMethod.getSignature(),
                                    safe(dslDocMethod.getDescription()))
                    );
            writer.format("|===%n");
            // Methods
            docClass.getMethods()
                    .stream()
                    .sorted(Comparator.comparing(DSLDocMethod::getName))
                    .forEach(
                            dslDocMethod -> adocMethod(writer, docClass, dslDocMethod, false)
                    );
        }
        // Separator
        writer.println();
    }

    private void adocPropertyClass(PrintWriter writer, DSLDocClass docClass, DSLDocClass propertyClass) {
        writer.format("[[dsl-%s-properties]]%n", docClass.getId());
        writer.format("|===%n");

        /*
         * Properties header
         */

        writer.format("2+h| Configuration properties%n");
        writer.format("2+a| %s%n%n%s%n%n",
                safe(propertyClass.getDescription()),
                safe(propertyClass.getLongDescription())
        );
        // See also section
        if (!propertyClass.getReferences().isEmpty()) {
            writer.format(
                    "%nSee also: %s%n",
                    propertyClass.getReferences().stream()
                            .map(AsciiDocGenerator::getRefLink)
                            .collect(Collectors.joining(", "))
            );
        }
        // Sample
        adocSample(writer, propertyClass.getSample());

        /*
         * Properties summary
         */

        writer.format("2+h| Configuration property summary%n");
        writer.format("| Property | Description%n%n");
        propertyClass.getMethods()
                .stream()
                .sorted(Comparator.comparing(DSLDocMethod::getName))
                .forEach(dslDocMethod ->
                        writer.format(
                                "| <<dsl-%s-%s,`%s`>> | `%s`%n%n%s%n%n",
                                propertyClass.getId(),
                                dslDocMethod.getId(),
                                dslDocMethod.getName(),
                                dslDocMethod.getSignature(),
                                safe(dslDocMethod.getDescription()))
                );

        /*
         * End of properties header
         */

        writer.format("|===%n");


        // Methods
        propertyClass.getMethods().forEach(
                dslDocMethod -> adocMethod(writer, propertyClass, dslDocMethod, true)
        );
        // Separator
        writer.println();
    }

    private void adocSample(PrintWriter writer, String sample) {
        if (StringUtils.isNotBlank(sample)) {
            writer.format("%nSample:%n");
            writer.format("%n[source,groovy]%n");
            writer.format("----%n");
            writer.println(sample);
            writer.format("----%n");
        }
    }

    private void adocMethod(PrintWriter writer, DSLDocClass docClass, DSLDocMethod docMethod, boolean property) {
        writer.format("%n[[dsl-%s-%s]]%n", docClass.getId(), docMethod.getId());
        writer.format("|===%n");
        writer.format("| %s%s%n%n", property ? "Configuration: " : "", docMethod.getName());
        writer.format("a| `%s`%n%n%s%n%n",
                docMethod.getSignature(),
                safe(docMethod.getDescription())
        );
        writer.format("%n");
        // References
        if (StringUtils.isNotBlank(docMethod.getSee())) {
            // Gets the target method
            DSLDocMethod targetMethod = docClass.getMethods().stream()
                    .filter(m -> StringUtils.equals(docMethod.getSee(), m.getId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(String.format(
                            "Cannot find method with ID <%s> in class <%s>",
                            docMethod.getSee(),
                            docClass.getName()
                    )));
            // Link
            writer.format(
                    "%nSee <<dsl-%s-%s,%s>>%n",
                    docClass.getId(),
                    docMethod.getSee(),
                    targetMethod.getName()
            );
        }
        if (!docMethod.getReferences().isEmpty()) {
            writer.format(
                    "%nSee: %s%n",
                    docMethod.getReferences().stream()
                            .map(AsciiDocGenerator::getRefLink)
                            .collect(Collectors.joining(", "))
            );
        }
        writer.format("%s%n%n", safe(docMethod.getLongDescription()));
        adocSample(writer, docMethod.getSample());
        writer.format("|===%n%n");

    }

    private String safe(String value) {
        return StringUtils.isNotBlank(value) ? value : "";
    }

    private static String getRefLink(DSLDocClass ref) {
        return String.format("<<dsl-%s,%s>>", ref.getId(), ref.getName());
    }

}
