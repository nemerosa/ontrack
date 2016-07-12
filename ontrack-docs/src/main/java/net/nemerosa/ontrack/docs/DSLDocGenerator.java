package net.nemerosa.ontrack.docs;

import net.nemerosa.ontrack.dsl.Ontrack;
import net.nemerosa.ontrack.dsl.doc.DSL;
import net.nemerosa.ontrack.json.ObjectMapperFactory;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Generation of DSL documentation
 */
public class DSLDocGenerator {

    public static void main(String[] args) throws IOException {
        String outputPath = args[0];
        // String version = args[1];

        File dir = new File(outputPath);
        //noinspection ResultOfMethodCallIgnored
        dir.mkdirs();

        DSLDoc doc = new DSLDocGenerator().generate(Ontrack.class);

        // Writing the output (JSON)
        File jsonFile = new File(dir, "dsl-generated.json");
        System.out.format("[doc] Writing JSON at %s%n", jsonFile);
        ObjectMapperFactory.create().writeValue(jsonFile, doc);

        // Writing the output (ADOC)
        File adocFile = new File(dir, "dsl-generated.adoc");
        System.out.format("[doc] Writing AsciiDoc at %s%n", adocFile);
        try (PrintWriter writer = new PrintWriter(adocFile)) {
            adoc(writer, doc);
        }
    }

    private static void adoc(PrintWriter writer, DSLDoc doc) {
        doc.getClasses().values().forEach(
                dslDocClass -> adocClass(writer, dslDocClass)
        );
    }

    private static void adocClass(PrintWriter writer, DSLDocClass docClass) {
        writer.format("[[dsl-%s]]%n", docClass.getId());
        writer.format("==== %s%n", docClass.getName());
        if (StringUtils.isNotBlank(docClass.getDescription())) {
            writer.format("%n%s%n", docClass.getDescription());
        }
        // Methods
        docClass.getMethods().forEach(
                dslDocMethod -> adocMethod(writer, docClass, dslDocMethod)
        );
    }

    private static void adocMethod(PrintWriter writer, DSLDocClass docClass, DSLDocMethod docMethod) {
        writer.format("%n[[dsl-%s-%s]]%n", docClass.getId(), docMethod.getId());
        writer.format("===== %s%n", docMethod.getName());
        if (StringUtils.isNotBlank(docMethod.getDescription())) {
            writer.format("%n%s%n", docMethod.getDescription());
        }
        if (StringUtils.isNotBlank(docMethod.getSample())) {
            writer.format("%n[source,groovy]%n");
            writer.format("----%n");
            writer.println(docMethod.getSample());
            writer.format("----%n");
        }
    }

    private DSLDoc generate(Class<?> clazz) throws IOException {
        // Doc
        DSLDoc doc = new DSLDoc();
        // Class description
        generateDocClass(doc, clazz);
        // OK
        return doc;
    }

    private void generateDocClass(DSLDoc doc, Class<?> clazz) throws IOException {
        DSL dsl = clazz.getAnnotation(DSL.class);
        if (dsl != null) {
            System.out.format("[doc] %s%n", clazz.getName());
            DSLDocClass docClass = new DSLDocClass(
                    clazz.getSimpleName(),
                    getClassDescription(dsl, clazz)
            );
            doc.getClasses().put(clazz.getName(), docClass);
            // Methods
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                generateDocMethod(doc, docClass, clazz, method);
            }
        }
    }

    private void generateDocMethod(DSLDoc doc, DSLDocClass docClass, Class<?> clazz, Method method) throws IOException {
        DSL methodDsl = method.getAnnotation(DSL.class);
        if (methodDsl != null) {
            DSLDocMethod docMethod = new DSLDocMethod(
                    getMethodName(methodDsl, method),
                    getMethodDescription(methodDsl, clazz, method),
                    getMethodSample(clazz, method)
            );
            docClass.getMethods().add(docMethod);
            // TODO Recursion on return & input parameters
        }
    }

    private String getMethodSample(Class<?> clazz, Method method) throws IOException {
        String path = String.format("/%s/%s.groovy", clazz.getName(), method.getName());
        InputStream in = clazz.getResourceAsStream(path);
        if (in != null) {
            return IOUtils.toString(in);
        } else {
            return null;
        }
    }

    private String getMethodDescription(DSL methodDsl, Class<?> clazz, Method method) throws IOException {
        return getDescription(methodDsl, clazz, String.format("/%s/%s", clazz.getName(), method.getName()));
    }

    private String getDescription(DSL dsl, Class<?> clazz, String id) throws IOException {
        String description = dsl.description();
        if (!Objects.equals(description, "")) {
            return description;
        } else {
            InputStream in = clazz.getResourceAsStream(String.format("%s.txt", id));
            if (in != null) {
                return IOUtils.toString(in);
            } else {
                return null;
            }
        }
    }

    private String getMethodName(DSL methodDsl, Method method) {
        return method.getName();
    }

    private String getClassDescription(DSL dsl, Class<?> clazz) throws IOException {
        return getDescription(dsl, clazz, String.format("/%s/description", clazz.getName()));
    }

}
