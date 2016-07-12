package net.nemerosa.ontrack.docs;

import net.nemerosa.ontrack.dsl.Ontrack;
import net.nemerosa.ontrack.dsl.doc.DSL;
import net.nemerosa.ontrack.json.ObjectMapperFactory;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
        dir.mkdirs();

        DSLDoc doc = new DSLDocGenerator().generate(Ontrack.class);

        // Writing the output (JSON)
        File jsonFile = new File(dir, "doc.json");
        System.out.format("[doc] Writing JSON at %s%n", jsonFile);
        ObjectMapperFactory.create().writeValue(jsonFile, doc);
        // TODO Writing the output (ADOC)
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
                    getMethodSample(methodDsl, clazz, method)
            );
            docClass.getMethods().add(docMethod);
            // TODO Recursion on return & input parameters
        }
    }

    private String getMethodSample(DSL methodDsl, Class<?> clazz, Method method) throws IOException {
        InputStream in = clazz.getResourceAsStream(String.format("%s-%s.groovy", clazz.getName(), method.getName()));
        if (in != null) {
            return IOUtils.toString(in);
        } else {
            return null;
        }
    }

    private String getMethodDescription(DSL methodDsl, Class<?> clazz, Method method) throws IOException {
        String description = methodDsl.description();
        if (!Objects.equals(description, "")) {
            return description;
        } else {
            InputStream in = clazz.getResourceAsStream(String.format("%s-%s.txt", clazz.getName(), method.getName()));
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
        String description = dsl.description();
        if (!Objects.equals(description, "")) {
            return description;
        } else {
            InputStream in = clazz.getResourceAsStream(String.format("%s.txt", clazz.getName()));
            if (in != null) {
                return IOUtils.toString(in);
            } else {
                return null;
            }
        }
    }

}
