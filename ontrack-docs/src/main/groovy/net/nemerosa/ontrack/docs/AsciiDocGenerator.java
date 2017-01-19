package net.nemerosa.ontrack.docs;

import groovy.text.GStringTemplateEngine;
import groovy.text.Template;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.stream.Collectors;

public class AsciiDocGenerator {

    public void generate(File dir, DSLDoc doc) throws IOException, ClassNotFoundException {
        // Index file
        File indexFile = new File(dir, "dsl-index.adoc");
        System.out.format("[doc] Writing index %s%n", indexFile.getName());
        try (PrintWriter writer = new PrintWriter(indexFile)) {
            adocIndex(writer, doc);
        }
        // TODO One file per class
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
        GStringTemplateEngine engine = new GStringTemplateEngine();
        // Loads the text
        String text = IOUtils.toString(AsciiDocGenerator.class.getResource("/templates/index.adoc"), "UTF-8");
        // Template
        Template template = engine.createTemplate(text);
        // Generates the content
        template.make(Collections.singletonMap("doc", doc)).writeTo(writer);
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
            adocPropertyClass(writer, docClass, propertyClass);
        }
        // Methods
        docClass.getMethods().forEach(
                dslDocMethod -> adocMethod(writer, docClass, dslDocMethod, false)
        );
        // Separator
        writer.println();
    }

    private void adocPropertyClass(PrintWriter writer, DSLDocClass docClass, DSLDocClass propertyClass) {
        writer.format("[[dsl-%s-properties]]%n", docClass.getId());
        writer.format("===== Properties%n");
        // Description
        if (StringUtils.isNotBlank(propertyClass.getDescription())) {
            writer.format("%n%s%n", propertyClass.getDescription());
        }
        if (StringUtils.isNotBlank(propertyClass.getLongDescription())) {
            writer.format("%n%s%n", propertyClass.getLongDescription());
        }
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
        // Methods
        propertyClass.getMethods().forEach(
                dslDocMethod -> adocMethod(writer, propertyClass, dslDocMethod, true)
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

    private void adocMethod(PrintWriter writer, DSLDocClass docClass, DSLDocMethod docMethod, boolean indent) {
        writer.format("%n[[dsl-%s-%s]]%n", docClass.getId(), docMethod.getId());
        writer.format("%s %s%n", StringUtils.repeat("=", indent ? 6 : 5), docMethod.getSignature());
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
