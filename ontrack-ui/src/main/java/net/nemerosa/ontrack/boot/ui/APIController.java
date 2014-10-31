package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.boot.support.APIMethodInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class APIController {

    private final ApplicationContext applicationContext;

    @Autowired
    public APIController(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @RequestMapping(value = "/handlers", method = RequestMethod.GET)
    public String show() {
        Collection<Object> controllers = applicationContext.getBeansWithAnnotation(Controller.class).values();
        controllers.forEach(controller -> {
            System.out.format("* %s%n", controller.getClass().getName());
            // Root request mapping
            RequestMapping typeAnnotation = AnnotationUtils.findAnnotation(controller.getClass(), RequestMapping.class);
            // Gets all the methods
            ReflectionUtils.doWithMethods(
                    controller.getClass(),
                    new ReflectionUtils.MethodCallback() {
                        @Override
                        public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                            RequestMapping methodAnnotation = AnnotationUtils.findAnnotation(method, RequestMapping.class);
                            if (methodAnnotation != null) {
                                APIMethodInfo apiMethodInfo = collectAPIMethodInfo(
                                        controller,
                                        method,
                                        typeAnnotation,
                                        methodAnnotation
                                );
                                System.out.format(
                                        "\t* %s %s%n",
                                        apiMethodInfo.getPath(),
                                        apiMethodInfo.getMethods()
                                );
                            }
                        }
                    },
                    ReflectionUtils.USER_DECLARED_METHODS
            );
        });
        return "ok";
    }

    protected APIMethodInfo collectAPIMethodInfo(
            Object controller,
            Method method,
            RequestMapping typeAnnotation,
            RequestMapping methodAnnotation) {
        // Path
        StringBuilder path = new StringBuilder();
        appendPath(typeAnnotation, path);
        appendPath(methodAnnotation, path);
        // Methods
        List<String> methods;
        List<RequestMethod> requestMethods = Arrays.asList(methodAnnotation.method());
        if (requestMethods.isEmpty()) {
            methods = Collections.singletonList("GET");
        } else {
            methods = requestMethods.stream().map(Enum::name).collect(Collectors.toList());
        }
        // OK
        return APIMethodInfo.of(path.toString(), methods);
    }

    private void appendPath(RequestMapping requestMapping, StringBuilder builder) {
        if (requestMapping != null) {
            String[] paths = requestMapping.value();
            if (paths != null && paths.length > 0) {
                String path = paths[0];
                if (builder.length() == 0 && path.startsWith("/")) {
                    path = StringUtils.strip(path, "/");
                } else if (builder.length() > 0) {
                    path = "/" + StringUtils.strip(path, "/");
                }
                builder.append(path);
            }
        }
    }
}
