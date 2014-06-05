package net.nemerosa.ontrack.model.structure;

import static org.apache.commons.lang3.Validate.isTrue;

/**
 * An <b>Entity</b> is a model object that has an {@link ID}. The state of this ID will determined the status
 * of this entity:
 * <ul>
 * <li><i>new</i> - the ID is not {@linkplain ID#isSet() set}.</li>
 * <li><i>defined</i> - the ID is {@linkplain ID#isSet() set}.</li>
 * </ul>
 */
public interface Entity {

    public static void isEntityNew(Entity e, String message) {
        isTrue(e != null && !ID.isDefined(e.getId()), message);
    }

    public static void isEntityDefined(Entity e, String message) {
        isTrue(e != null && ID.isDefined(e.getId()), message);
    }

    ID getId();

    default int id() {
        ID id = getId();
        isTrue(ID.isDefined(id), "ID must be defined");
        return getId().getValue();
    }

}
