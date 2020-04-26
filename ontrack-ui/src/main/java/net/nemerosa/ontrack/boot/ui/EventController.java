package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.events.Event;
import net.nemerosa.ontrack.model.events.EventQueryService;
import net.nemerosa.ontrack.model.exceptions.PropertyTypeNotFoundException;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.model.support.NameValue;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import net.nemerosa.ontrack.ui.resource.Pagination;
import net.nemerosa.ontrack.ui.resource.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

/**
 * Access to the events
 */
@RestController
@RequestMapping("/rest/events")
public class EventController extends AbstractResourceController {

    private final Logger logger = LoggerFactory.getLogger(EventController.class);

    private final EventQueryService eventQueryService;
    private final PropertyService propertyService;

    @Autowired
    public EventController(EventQueryService eventQueryService, PropertyService propertyService) {
        this.eventQueryService = eventQueryService;
        this.propertyService = propertyService;
    }

    /**
     * Gets the list of events for the root.
     */
    @RequestMapping(value = "root", method = RequestMethod.GET)
    public Resources<UIEvent> getEvents(
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "20") int count) {
        // Gets the events
        Resources<UIEvent> resources = Resources.of(
                eventQueryService.getEvents(offset, count).stream()
                        .map(this::toUIEvent)
                        .collect(Collectors.toList()),
                uri(on(getClass()).getEvents(offset, count))).forView(UIEvent.class);
        // Pagination information
        Pagination pagination = Pagination.of(offset, count, -1);
        // Previous page
        if (offset > 0) {
            pagination = pagination.withPrev(
                    uri(on(EventController.class).getEvents(
                            Math.max(0, offset - count),
                            count
                    ))
            );
        }
        // Next page
        pagination = pagination.withNext(
                uri(on(EventController.class).getEvents(
                        offset + count,
                        count
                ))
        );
        return resources.withPagination(pagination);
    }

    /**
     * Gets the list of events for an entity, accessible by the current user.
     */
    @RequestMapping(value = "{entityType}/{entityId}", method = RequestMethod.GET)
    public Resources<UIEvent> getEvents(
            @PathVariable ProjectEntityType entityType,
            @PathVariable ID entityId,
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "10") int count) {
        // Gets the events
        Resources<UIEvent> resources = Resources.of(
                eventQueryService.getEvents(entityType, entityId, offset, count).stream()
                        .map(this::toUIEvent)
                        .collect(Collectors.toList()),
                uri(on(getClass()).getEvents(entityType, entityId, offset, count))).forView(UIEvent.class);
        // Pagination information
        Pagination pagination = Pagination.of(offset, count, -1);
        // Previous page
        if (offset > 0) {
            pagination = pagination.withPrev(
                    uri(on(EventController.class).getEvents(
                            entityType,
                            entityId,
                            Math.max(0, offset - count),
                            count
                    ))
            );
        }
        // Next page
        pagination = pagination.withNext(
                uri(on(EventController.class).getEvents(
                        entityType,
                        entityId,
                        offset + count,
                        count
                ))
        );
        return resources.withPagination(pagination);
    }

    protected UIEvent toUIEvent(Event event) {
        return new UIEvent(
                event.getEventType().getId(),
                event.getEventType().getTemplate(),
                event.getSignature(),
                event.getEntities(),
                event.getRef(),
                event.getValues(),
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
