package net.nemerosa.ontrack.docs;

import net.nemerosa.ontrack.dsl.doc.DSL;
import net.nemerosa.ontrack.dsl.doc.DSLMethod;
import net.nemerosa.ontrack.dsl.doc.DSLProperties;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.groovydoc.GroovyMethodDoc;
import org.codehaus.groovy.groovydoc.GroovyParameter;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * Generation of DSL documentation
 */
public class DSLDocExtractor {

    private final GroovyDocHelper groovyDocHelper;

    public DSLDocExtractor(String sourcePath) {
        this.groovyDocHelper = new GroovyDocHelper(sourcePath);
    }

    public DSLDoc generate(Class<?> clazz) throws IOException {
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
                        getClassDescription(dsl),
                        getClassLongDescription(clazz),
                        getClassSample(clazz),
                        clazz.getAnnotation(DSLProperties.class) != null
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
        DSLMethod methodDsl = method.getAnnotation(DSLMethod.class);
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
                        getMethodSample(methodDsl, clazz, method),
                        methodDsl.see()
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
                    if (dslDocClass.isPropertyClass()) {
                        docClass.getProperties().set(dslDocClass);
                    } else {
                        docMethod.getReferences().add(dslDocClass);
                    }
                }
            }
        }
    }

    private String getMethodId(DSLMethod methodDsl, Method method) {
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

    private String getMethodSample(DSLMethod methodDsl, Class<?> clazz, Method method) throws IOException {
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

    private String getMethodDescription(DSLMethod methodDsl) throws IOException {
        if (StringUtils.isNotBlank(methodDsl.value())) {
            return methodDsl.value();
        } else {
            return null;
        }
    }

    private String getMethodLongDescription(DSLMethod methodDsl, Class<?> clazz, Method method) throws IOException {
        InputStream in = clazz.getResourceAsStream(String.format("/%s/%s.adoc", method.getDeclaringClass().getName(), getMethodId(methodDsl, method)));
        if (in != null) {
            return IOUtils.toString(in);
        } else {
            return null;
        }
    }

    private String getClassLongDescription(Class<?> clazz) throws IOException {
        InputStream in = clazz.getResourceAsStream(String.format("/%s/description.adoc", clazz.getName()));
        if (in != null) {
            return IOUtils.toString(in);
        } else {
            return null;
        }
    }

    private String getMethodName(Method method) {
        return method.getName();
    }

    private String getClassDescription(DSL dsl) throws IOException {
        if (StringUtils.isNotBlank(dsl.value())) {
            return dsl.value();
        } else {
            return null;
        }
    }

}
