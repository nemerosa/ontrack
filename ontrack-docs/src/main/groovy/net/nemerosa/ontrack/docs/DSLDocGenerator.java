package net.nemerosa.ontrack.docs;

import net.nemerosa.ontrack.dsl.Ontrack;
import net.nemerosa.ontrack.dsl.doc.DSL;
import net.nemerosa.ontrack.json.ObjectMapperFactory;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.groovydoc.GroovyMethodDoc;
import org.codehaus.groovy.groovydoc.GroovyParameter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Generation of DSL documentation
 */
public class DSLDocGenerator {

    private final GroovyDocHelper groovyDocHelper;

    public DSLDocGenerator(String sourcePath) {
        this.groovyDocHelper = new GroovyDocHelper(sourcePath);
    }

    public static void main(String[] args) throws IOException {
        String sourcePath = args[0];
        String outputPath = args[1];
        // String version = args[1];

        File dir = new File(outputPath);
        //noinspection ResultOfMethodCallIgnored
        dir.mkdirs();

        DSLDoc doc = new DSLDocGenerator(sourcePath).generate(Ontrack.class);

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
        // Description
        if (StringUtils.isNotBlank(docClass.getDescription())) {
            writer.format("%n%s%n", docClass.getDescription());
        }
        // See also section
        if (!docClass.getReferences().isEmpty()) {
            writer.format(
                    "%nSee also: %s%n",
                    docClass.getReferences().stream()
                            .map(DSLDocGenerator::getRefLink)
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

    private static void adocSample(PrintWriter writer, String sample) {
        if (StringUtils.isNotBlank(sample)) {
            writer.format("%n[source,groovy]%n");
            writer.format("----%n");
            writer.println(sample);
            writer.format("----%n");
        }
    }

    private static void adocMethod(PrintWriter writer, DSLDocClass docClass, DSLDocMethod docMethod) {
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
                            .map(DSLDocGenerator::getRefLink)
                            .collect(Collectors.joining(", "))
            );
        }
    }

    private static String getRefLink(DSLDocClass ref) {
        return String.format("<<dsl-%s,%s>>", ref.getId(), ref.getName());
    }

    private DSLDoc generate(Class<?> clazz) throws IOException {
        // Doc
        DSLDoc doc = new DSLDoc();
        // Class description
        generateDocClass(doc, clazz);
        // OK
        return doc;
    }

    private DSLDocClass generateDocClass(DSLDoc doc, Class<?> clazz) throws IOException {
        DSL dsl = clazz.getAnnotation(DSL.class);
        if (dsl != null) {
            DSLDocClass dslDocClass = doc.getClasses().get(clazz.getName());
            if (dslDocClass == null) {
                System.out.format("[doc] %s%n", clazz.getName());
                dslDocClass = new DSLDocClass(
                        clazz.getSimpleName(),
                        getClassDescription(dsl, clazz),
                        getClassSample(clazz)
                );
                doc.getClasses().put(clazz.getName(), dslDocClass);
                // Methods
                Method[] methods = clazz.getDeclaredMethods();
                for (Method method : methods) {
                    generateDocMethod(doc, dslDocClass, clazz, method);
                }
                // Super classes
                Class<?> superclass = clazz.getSuperclass();
                DSLDocClass dslSuperClass = generateDocClass(doc, superclass);
                if (dslSuperClass != null) {
                    dslDocClass.getReferences().add(dslSuperClass);
                }
                // OK
                return dslDocClass;
            } else {
                return dslDocClass;
            }
        } else {
            return null;
        }
    }

    private void generateDocMethod(DSLDoc doc, DSLDocClass docClass, Class<?> clazz, Method method) throws IOException {
        DSL methodDsl = method.getAnnotation(DSL.class);
        if (methodDsl != null) {
            // Checks if the method is consistent with the Groovy signature
            boolean consistent = methodDsl.count() < 0 || methodDsl.count() == method.getParameterTypes().length;
            if (consistent) {
                // Tries to find the Groovy method documentation
                GroovyMethodDoc groovyMethodDoc = groovyDocHelper.getAllMethods(clazz).stream()
                        .filter(gm -> StringUtils.equals(gm.name(), method.getName()))
                        .filter(gm -> Objects.equals(GroovyDocHelper.getMethodFromGroovyMethodDoc(gm, clazz), method))
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("Cannot find doc for method: " + method));
                // OK
                DSLDocMethod docMethod = new DSLDocMethod(
                        getMethodId(methodDsl, method),
                        getMethodName(method),
                        getMethodSignature(groovyMethodDoc, method),
                        getMethodDescription(methodDsl),
                        getMethodLongDescription(methodDsl, clazz, method),
                        getMethodSample(methodDsl, clazz, method)
                );
                docClass.getMethods().add(docMethod);
                // Return type
                DSLDocClass dslDocClass = null;
                Type genericReturnType = method.getGenericReturnType();
                if (genericReturnType instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) genericReturnType;
                    for (Type type : parameterizedType.getActualTypeArguments()) {
                        if (type instanceof Class) {
                            dslDocClass = generateDocClass(doc, (Class) type);
                        }
                    }
                } else {
                    dslDocClass = generateDocClass(doc, method.getReturnType());
                }
                // Reference?
                if (dslDocClass != null && !StringUtils.equals(docClass.getId(), dslDocClass.getId())) {
                    docMethod.getReferences().add(dslDocClass);
                }
            }
        }
    }

    private String getMethodId(DSL methodDsl, Method method) {
        if (StringUtils.isNotBlank(methodDsl.id())) {
            return methodDsl.id();
        } else {
            return method.getName();
        }
    }

    private String getMethodSignature(GroovyMethodDoc groovyMethodDoc, Method method) {
        StringBuilder s = new StringBuilder();
        // Return
        Type genericReturnType = method.getGenericReturnType();
        if (genericReturnType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericReturnType;
            s.append(method.getReturnType().getSimpleName());
            s.append("<");
            for (int i = 0; i < parameterizedType.getActualTypeArguments().length; i++) {
                Type type = parameterizedType.getActualTypeArguments()[i];
                if (type instanceof Class) {
                    if (i > 0) s.append(",");
                    s.append(((Class) type).getSimpleName());
                }
            }
            s.append(">");
        } else {
            s.append(method.getReturnType().getSimpleName());
        }
        // Space + name
        s.append(" ").append(method.getName());
        // Parameters
        s.append("(");
        GroovyParameter[] parameters = groovyMethodDoc.parameters();
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            GroovyParameter gp = parameters[i];
            String name = gp.name();
            if (i > 0) s.append(", ");
            s.append(parameterType.getSimpleName()).append(" ").append(name);
            String defaultValue = gp.defaultValue();
            if (defaultValue != null) {
                s.append(" = ").append(defaultValue);
            }
        }
        s.append(")");
        // OK
        return s.toString();
    }

    private String getMethodSample(DSL methodDsl, Class<?> clazz, Method method) throws IOException {
        String path = String.format("/%s/%s.groovy", method.getDeclaringClass().getName(), getMethodId(methodDsl, method));
        InputStream in = clazz.getResourceAsStream(path);
        if (in != null) {
            return IOUtils.toString(in);
        } else {
            return null;
        }
    }

    private String getClassSample(Class<?> clazz) throws IOException {
        String path = String.format("/%s/sample.groovy", clazz.getName());
        InputStream in = clazz.getResourceAsStream(path);
        if (in != null) {
            return IOUtils.toString(in);
        } else {
            return null;
        }
    }

    private String getMethodDescription(DSL methodDsl) throws IOException {
        if (StringUtils.isNotBlank(methodDsl.value())) {
            return methodDsl.value();
        } else {
            return null;
        }
    }

    private String getMethodLongDescription(DSL methodDsl, Class<?> clazz, Method method) throws IOException {
        InputStream in = clazz.getResourceAsStream(String.format("/%s/%s.txt", method.getDeclaringClass().getName(), getMethodId(methodDsl, method)));
        if (in != null) {
            return IOUtils.toString(in);
        } else {
            return null;
        }
    }

    private String getDescription(DSL dsl, Class<?> clazz, String id) throws IOException {
        String description = dsl.value();
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

    private String getMethodName(Method method) {
        return method.getName();
    }

    private String getClassDescription(DSL dsl, Class<?> clazz) throws IOException {
        return getDescription(dsl, clazz, String.format("/%s/description", clazz.getName()));
    }

}
