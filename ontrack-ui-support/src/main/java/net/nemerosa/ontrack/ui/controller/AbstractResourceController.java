package net.nemerosa.ontrack.ui.controller;

import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.net.URI;

public abstract class AbstractResourceController {

    /**
     * @see org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder#fromMethodCall(Object)
     */
    protected URI uri(Object methodInvocation) {
        return MvcUriComponentsBuilder.fromMethodCall(methodInvocation).build().toUri();
    }

}
