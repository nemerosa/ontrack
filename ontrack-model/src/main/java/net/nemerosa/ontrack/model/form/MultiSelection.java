package net.nemerosa.ontrack.model.form;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Data
public class MultiSelection extends AbstractField<MultiSelection> {

    public static MultiSelection of(String name) {
        return new MultiSelection(name);
    }

    private List<?> items = new ArrayList<>();
    private String itemId = "id";
    private String itemName = "name";

    protected MultiSelection(String name) {
        super("multi-selection", name);
    }

    public MultiSelection items(List<?> values) {
        this.items = values;
        return this;
    }

    public MultiSelection itemId(String value) {
        this.itemId = value;
        return this;
    }

    public MultiSelection itemName(String value) {
        this.itemName = value;
        return this;
    }
}
