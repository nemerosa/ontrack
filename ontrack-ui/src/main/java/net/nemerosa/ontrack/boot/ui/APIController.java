package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.boot.support.APIDescription;
import net.nemerosa.ontrack.boot.support.APIInfo;
import net.nemerosa.ontrack.boot.support.APIMethodInfo;
import net.nemerosa.ontrack.common.RunProfile;
import net.nemerosa.ontrack.model.exceptions.APIMethodInfoNotFoundException;
import net.nemerosa.ontrack.model.structure.NameDescription;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import net.nemerosa.ontrack.ui.resource.Resources;
import net.nemerosa.ontrack.ui.support.API;
import net.nemerosa.ontrack.ui.support.APIMethod;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static net.nemerosa.ontrack.model.structure.NameDescription.nd;
import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@RestController
@RequestMapping("/api")
@Profile({RunProfile.DEV, RunProfile.ACC, RunProfile.PROD})
public class APIController extends AbstractResourceController {

    private final ApplicationContext applicationContext;
    private final RequestMappingHandlerMapping handlerMapping;

    @Autowired
    public APIController(
            ApplicationContext applicationContext,
            @Qualifier("requestMappingHandlerMapping")
                    RequestMappingHandlerMapping handlerMapping) {
        this.applicationContext = applicationContext;
        this.handlerMapping = handlerMapping;
    }

    @RequestMapping(value = "/describe", method = RequestMethod.GET)
    public APIDescription describe(HttpServletRequest request, @RequestParam String path) throws Exception {
        HandlerExecutionChain executionChain = handlerMapping.getHandler(
                new HttpServletRequestWrapper(request) {
                    @Override
                    public String getRequestURI() {
                        return path;
                    }

                    @Override
                    public String getServletPath() {
                        return path;
                    }
                }
        );
        // Gets the handler
        Object handler = executionChain.getHandler();
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            String type = handlerMethod.getBeanType().getName();
            String method = handlerMethod.getMethod().getName();
            // Gets all the infos
            List<APIInfo> apiInfos = getApiInfos();
            // Looking for any GET mapping
            APIMethodInfo get = apiInfos.stream()
                    .flatMap(i -> i.getMethods().stream())
                    .filter(mi -> StringUtils.equals(type, mi.getApiInfo().getType())
                            && StringUtils.equals(method, mi.getMethod()))
                    .findFirst()
                    .orElseThrow(() -> new APIMethodInfoNotFoundException(path));
            // Gets all methods with the same path pattern
            List<APIMethodInfo> methods = apiInfos.stream()
                    .flatMap(i -> i.getMethods().stream())
                    .filter(mi -> StringUtils.equals(get.getPath(), mi.getPath()))
                    .collect(Collectors.toList());
            // OK
            return new APIDescription(
                    path,
                    methods
            );
        } else {
            throw new APIMethodInfoNotFoundException(path);
        }
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Resources<APIInfo> list() {
        return Resources.of(
                getApiInfos(),
                uri(on(getClass()).list())
        );
    }

    private List<APIInfo> getApiInfos() {
        List<APIInfo> apiInfos = new ArrayList<>();
        Collection<Object> controllers = applicationContext.getBeansWithAnnotation(Controller.class).values();
        controllers.forEach(controller -> {
            APIInfo apiInfo = new APIInfo(
                    cleanProxiedClassName(controller.getClass()),
                    getAPIName(controller.getClass())
            );
            // Root request mapping
            RequestMapping typeAnnotation = AnnotationUtils.findAnnotation(controller.getClass(), RequestMapping.class);
            // Gets all the methods
            ReflectionUtils.doWithMethods(
                    controller.getClass(),
                    method -> {
                        RequestMapping methodAnnotation = AnnotationUtils.findAnnotation(method, RequestMapping.class);
                        if (methodAnnotation != null) {
                            APIMethodInfo apiMethodInfo = collectAPIMethodInfo(
                                    apiInfo,
                                    method,
                                    typeAnnotation,
                                    methodAnnotation
                            );
                            apiInfo.add(apiMethodInfo);
                        }
                    },
                    ReflectionUtils.USER_DECLARED_METHODS
            );
            // OK for this API
            apiInfos.add(apiInfo);
        });
        return apiInfos;
    }

    private String cleanProxiedClassName(Class<?> controllerClass) {
        return StringUtils.substringBefore(
                controllerClass.getName(),
                "$$" // Cleanup proxied classed
        );
    }

    private NameDescription getAPIName(Class<?> controllerClass) {
        API api = AnnotationUtils.findAnnotation(controllerClass, API.class);
        return api != null ?
                nd(api.value(), api.description()) :
                nd(
                        capitalize(
                                asDisplayName(
                                        StringUtils.removeEnd(cleanProxiedClassName(controllerClass), "Controller")
                                )
                        ),
                        ""
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

    private NameDescription getAPIMethodName(Method method) {
        APIMethod api = AnnotationUtils.findAnnotation(method, APIMethod.class);
        return api != null ?
                nd(api.value(), api.description()) :
                nd(capitalize(asDisplayName(method.getName())), "");
    }

    protected APIMethodInfo collectAPIMethodInfo(
            APIInfo apiInfo,
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
        return APIMethodInfo.of(apiInfo, method.getName(), getAPIMethodName(method), path.toString(), methods);
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
