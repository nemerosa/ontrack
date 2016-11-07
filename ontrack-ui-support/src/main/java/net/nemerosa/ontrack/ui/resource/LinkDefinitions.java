package net.nemerosa.ontrack.ui.resource;

import net.nemerosa.ontrack.model.security.GlobalFunction;
import net.nemerosa.ontrack.model.security.ProjectFunction;
import net.nemerosa.ontrack.model.structure.ProjectEntity;

import java.util.function.BiFunction;
import java.util.function.Function;

public class LinkDefinitions {

    public static <T extends ProjectEntity, P extends ProjectFunction> BiFunction<T, ResourceContext, Boolean> withProjectFn(Class<P> projectFn) {
        return (T e, ResourceContext resourceContext) -> resourceContext.isProjectFunctionGranted(e, projectFn);
    }

    public static <T extends ProjectEntity, G extends GlobalFunction> BiFunction<T, ResourceContext, Boolean> withGlobalFn(Class<G> globalFn) {
        return (T e, ResourceContext resourceContext) -> resourceContext.isGlobalFunctionGranted(globalFn);
    }

    public static <T extends ProjectEntity> LinkDefinition<T> link(String name, BiFunction<T, ResourceContext, Object> linkFn,
                                                                   BiFunction<T, ResourceContext, Boolean> checkFn) {
        return new SimpleLinkDefinition<>(
                name,
                linkFn,
                checkFn
        );
    }

    public static <T extends ProjectEntity> LinkDefinition<T> link(String name, BiFunction<T, ResourceContext, Object> linkFn) {
        return link(
                name,
                linkFn,
                (t, rc) -> true
        );
    }

    public static <T extends ProjectEntity> LinkDefinition<T> link(String name, Function<T, Object> linkFn) {
        return link(
                name,
                (t, resourceContext) -> linkFn.apply(t)
        );
    }

    public static <T extends ProjectEntity> LinkDefinition<T> self(Function<T, Object> linkFn) {
        return link(
                Link.SELF,
                linkFn
        );
    }

    public static <T extends ProjectEntity> LinkDefinition<T> link(String name, Function<T, Object> linkFn, BiFunction<T, ResourceContext, Boolean> checkFn) {
        return link(
                name,
                (t, resourceContext) -> linkFn.apply(t),
                checkFn
        );
    }

    public static <T extends ProjectEntity, P extends ProjectFunction> LinkDefinition<T> delete(Function<T, Object> linkFn, Class<P> fn) {
        return link(
                Link.DELETE,
                linkFn,
                withProjectFn(fn)
        );
    }

    /**
     * Creation of a link to the entity's page
     */
    public static <T extends ProjectEntity> LinkDefinition<T> page() {
        return new EntityPageLinkDefinition<>(
                (e, rc) -> true
        );
    }

    public static <T extends ProjectEntity, P extends ProjectFunction> LinkDefinition<T> page(String name, Class<P> projectFn, String path, Object... arguments) {
        return page(
                name,
                withProjectFn(projectFn),
                path, arguments
        );
    }

    public static <T extends ProjectEntity> LinkDefinition<T> page(String name, BiFunction<T, ResourceContext, Boolean> checkFn, String path, Object... arguments) {
        return new PagePathLinkDefinition<>(
                name,
                String.format(path, arguments),
                checkFn
        );
    }
}
