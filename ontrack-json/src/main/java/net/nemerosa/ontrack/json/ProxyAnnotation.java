package net.nemerosa.ontrack.json;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

public final class ProxyAnnotation
{
    @SuppressWarnings("unchecked")
    public static <A extends Annotation> A of(Class<A> annotationType, Map<String, ?> properties)
    {
        return (A) Proxy.newProxyInstance(annotationType.getClassLoader(),
                new Class[]{annotationType}, new AnnotationInvocationHandler<A>(annotationType,
                properties));
    }

    private static class AnnotationInvocationHandler<A extends Annotation> implements
            InvocationHandler
    {
        private final Class<A> annotationType;
        private final Map<String, ?> properties;

        private AnnotationInvocationHandler(Class<A> annotationType, Map<String, ?> properties)
        {
            this.annotationType = annotationType;
            this.properties = properties;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
        {
            if (method.getName().equals("annotationType"))
            {
                return annotationType;
            }
            Object value = properties.get(method.getName());
            if (value != null)
            {
                return value;
            }
            return method.getDefaultValue();
        }
    }

    private ProxyAnnotation()
    {
        // prevent instantiation
    }
}
