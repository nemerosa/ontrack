package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.events.Event;
import net.nemerosa.ontrack.model.events.EventQueryService;
import net.nemerosa.ontrack.model.events.EventTemplatingService;
import net.nemerosa.ontrack.model.events.HtmlNotificationEventRenderer;
import net.nemerosa.ontrack.model.exceptions.PropertyTypeNotFoundException;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.model.support.NameValue;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Access to the events
 */
@RestController
@RequestMapping("/rest/events")
public class EventController extends AbstractResourceController {

    private final Logger logger = LoggerFactory.getLogger(EventController.class);

    private final EventQueryService eventQueryService;
    private final PropertyService propertyService;
    private final HtmlNotificationEventRenderer htmlNotificationEventRenderer;
    private final EventTemplatingService eventTemplatingService;

    @Autowired
    public EventController(EventQueryService eventQueryService, PropertyService propertyService, HtmlNotificationEventRenderer htmlNotificationEventRenderer, EventTemplatingService eventTemplatingService) {
        this.eventQueryService = eventQueryService;
        this.propertyService = propertyService;
        this.htmlNotificationEventRenderer = htmlNotificationEventRenderer;
        this.eventTemplatingService = eventTemplatingService;
    }

    /**
     * Gets the list of events for the root.
     */
    @RequestMapping(value = "root", method = RequestMethod.GET)
    public List<UIEvent> getEvents(
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "20") int count) {
        return eventQueryService.getEvents(offset, count).stream()
                        .map(this::toUIEvent)
                        .collect(Collectors.toList());
    }

    /**
     * Gets the list of events for an entity, accessible by the current user.
     */
    @RequestMapping(value = "{entityType}/{entityId}", method = RequestMethod.GET)
    public List<UIEvent> getEvents(
            @PathVariable ProjectEntityType entityType,
            @PathVariable ID entityId,
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "10") int count) {
        // Gets the events
        return eventQueryService.getEvents(entityType, entityId, offset, count).stream()
                        .map(this::toUIEvent)
                        .collect(Collectors.toList());
    }

    protected UIEvent toUIEvent(Event event) {
        return new UIEvent(
                event.getEventType().getId(),
                event.getEventType().getTemplate(),
                event.getSignature(),
                event.getEntities(),
                event.getExtraEntities(),
                event.getRef(),
                event.getValues(),
                eventTemplatingService.renderEvent(
                    event,
                    Collections.emptyMap(),
                    null,
                    htmlNotificationEventRenderer
                ),
                computeData(event)
        );
    }

    protected Map<String, ?> computeData(Event event) {
        // Result
        Map<String, ? super Object> result = new HashMap<>();
        // Any property in values?
        NameValue property = event.getValues().get("property");
        if (property != null) {
            String propertyName = property.getName();
            // Gets the property type by name
            try {
                PropertyType<?> propertyType = propertyService.getPropertyTypeByName(propertyName);
                result.put(
                        "property",
                        PropertyTypeDescriptor.of(propertyType)
                );
            } catch (PropertyTypeNotFoundException ignored) {
                // Logs and ignores
                logger.warn("[event] Could not find property type for {}", propertyName);
            }
        }
        // OK
        return result;
    }

}
