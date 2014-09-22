package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.events.Event;
import net.nemerosa.ontrack.model.events.EventQueryService;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import net.nemerosa.ontrack.ui.resource.Pagination;
import net.nemerosa.ontrack.ui.resource.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

/**
 * Access to the events
 */
@RestController
@RequestMapping("/events")
public class EventController extends AbstractResourceController {

    private final EventQueryService eventQueryService;

    @Autowired
    public EventController(EventQueryService eventQueryService) {
        this.eventQueryService = eventQueryService;
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
                event.getTemplate(),
                event.getSignature(),
                event.getEntities(),
                event.getValues()
        );
    }

}
