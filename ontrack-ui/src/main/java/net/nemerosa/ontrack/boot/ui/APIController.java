package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.boot.support.APIInfo;
import net.nemerosa.ontrack.boot.support.APIMethodInfo;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import net.nemerosa.ontrack.ui.resource.Resources;
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
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@RestController
@RequestMapping("/api")
public class APIController extends AbstractResourceController {

    private final ApplicationContext applicationContext;

    @Autowired
    public APIController(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @RequestMapping(value = "/handlers", method = RequestMethod.GET)
    public Resources<APIInfo> show() {
        List<APIInfo> apiInfos = new ArrayList<>();
        Collection<Object> controllers = applicationContext.getBeansWithAnnotation(Controller.class).values();
        controllers.forEach(controller -> {
            APIInfo apiInfo = new APIInfo(getAPIName(controller.getClass()));
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
                                apiInfo.add(apiMethodInfo);
                            }
                        }
                    },
                    ReflectionUtils.USER_DECLARED_METHODS
            );
            // OK for this API
            apiInfos.add(apiInfo);
        });
        // Resources
        return Resources.of(
                apiInfos,
                uri(on(getClass()).show())
        );
    }

    private String getAPIName(Class<?> controllerClass) {
        // TODO Use annotations
        return capitalize(
                asDisplayName(
                        StringUtils.removeEnd(controllerClass.getSimpleName(), "Controller")
                )
        );

    }

    private static String asDisplayName(CharSequence name) {
        StringBuilder s = new StringBuilder();
        name.codePoints().forEach(c -> {
            if (s.length() > 0 && !Character.isUpperCase(s.codePointAt(s.length() - 1)) && Character.isUpperCase(c)) {
                s.append(' ').append(Character.toChars(Character.toLowerCase(c)));
            } else {
                s.append(Character.toChars(c));
            }
        });
        return s.toString();
    }

    private String getAPIMethodName(Method method) {
        // TODO Use annotations
        return capitalize(asDisplayName(method.getName()));
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
        return APIMethodInfo.of(getAPIMethodName(method), path.toString(), methods);
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
