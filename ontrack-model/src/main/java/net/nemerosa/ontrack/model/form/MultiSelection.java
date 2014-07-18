package net.nemerosa.ontrack.model.form;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.nemerosa.ontrack.model.support.Selectable;

import java.util.ArrayList;
import java.util.List;

/**
 * This field allows a user to select several items into a list.
 * <p>
 * The {@link #getItems() items} property contains the {@link net.nemerosa.ontrack.model.support.Selectable selectable}
 * items.
 */
@EqualsAndHashCode(callSuper = false)
@Data
public class MultiSelection extends AbstractField<MultiSelection> {

    public static MultiSelection of(String name) {
        return new MultiSelection(name);
    }

    private List<? extends Selectable> items = new ArrayList<>();

    protected MultiSelection(String name) {
        super("multi-selection", name);
    }

    public MultiSelection items(List<? extends Selectable> values) {
        this.items = values;
        return this;
    }

}
