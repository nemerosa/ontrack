package net.nemerosa.ontrack.model.structure;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.nemerosa.ontrack.model.exceptions.SyncTargetItemPresentException;
import net.nemerosa.ontrack.model.exceptions.SyncTargetItemUnknownException;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Behaviour to adopt when synchronising two lists.
 */
@Data
public class SyncPolicy {

    /**
     * Target already present.
     * <p>
     * <ul>
     * <li>Can be ignored</li>
     * <li>Can be replaced</li>
     * <li>Can raise an error</li>
     * </ul>
     */
    private final TargetPresentPolicy targetPresentPolicy;

    /**
     * Unknown target.
     * <p>
     * <ul>
     * <li>Can be ignored</li>
     * <li>Can be deleted</li>
     * <li>Can raise an error</li>
     * </ul>
     */
    private final UnknownTargetPolicy unknownTargetPolicy;

    public static final SyncPolicy COPY = new SyncPolicy(TargetPresentPolicy.IGNORE, UnknownTargetPolicy.IGNORE);
    public static final SyncPolicy SYNC = new SyncPolicy(TargetPresentPolicy.REPLACE, UnknownTargetPolicy.DELETE);

    public static enum TargetPresentPolicy {
        IGNORE,
        REPLACE,
        ERROR
    }

    public static enum UnknownTargetPolicy {
        IGNORE,
        DELETE,
        ERROR
    }

    @Data
    public static class SyncConfig<T, D> {

        private final String itemType;
        private final Supplier<Collection<T>> sourceItems;
        private final Supplier<Collection<T>> targetItems;
        private final Function<T, D> itemId;
        private final Function<D, Optional<T>> targetItem;
        private final Consumer<T> createTargetItem;
        private final BiConsumer<T, T> replaceTargetItem;
        private final Consumer<T> deleteTargetItem;

    }

    @ToString
    public static class SyncResult {

        @Getter
        @Setter
        private int unknownTargetIgnored = 0;
        @Getter
        @Setter
        private int unknownTargetDeleted = 0;
        @Getter
        private int created = 0;
        @Getter
        private int presentTargetIgnored = 0;
        @Getter
        private int presentTargetReplaced = 0;

        public static SyncResult empty() {
            return new SyncResult();
        }

        public void create() {
            created++;
        }

        public void ignorePresentTarget() {
            presentTargetIgnored++;
        }

        public void replacePresentTarget() {
            presentTargetReplaced++;
        }
    }

    public <T, D> SyncResult sync(SyncConfig<T, D> config) {
        SyncResult result = SyncResult.empty();
        // Gets the source items
        Collection<T> sourceItems = config.getSourceItems().get();
        // For each source item
        for (T sourceItem : sourceItems) {
            // Gets its identifier for the target items
            D itemId = config.getItemId().apply(sourceItem);
            // Gets a corresponding item
            Optional<T> targetItemOpt = config.getTargetItem().apply(itemId);
            // If present
            if (targetItemOpt.isPresent()) {
                // This depends on the policy
                switch (targetPresentPolicy) {
                    case IGNORE:
                        result.ignorePresentTarget();
                        break;
                    case REPLACE:
                        config.getReplaceTargetItem().accept(sourceItem, targetItemOpt.get());
                        result.replacePresentTarget();
                        break;
                    case ERROR:
                    default:
                        throw new SyncTargetItemPresentException(config.getItemType(), itemId);
                }
            }
            // If not present, creates it
            else {
                config.getCreateTargetItem().accept(sourceItem);
                result.create();
            }
        }
        // Now that target items have been either created or updated, we need to know which ones were
        // not matched with the sources
        Map<D, T> targetMap = config.getTargetItems().get().stream().collect(
                Collectors.toMap(
                        config.getItemId(),
                        Function.identity()
                )
        );
        Collection<D> targetIds = new HashSet<>(targetMap.keySet());
        Collection<D> sourceIds = sourceItems.stream().map(config.getItemId()).collect(Collectors.toList());
        targetIds.removeAll(sourceIds);
        int unknownTargetNumber = targetIds.size();
        if (unknownTargetNumber > 0) {
            switch (unknownTargetPolicy) {
                case IGNORE:
                    result.setUnknownTargetIgnored(unknownTargetNumber);
                    break;
                case DELETE:
                    for (T targetItem : targetMap.values()) {
                        config.getDeleteTargetItem().accept(targetItem);
                    }
                    result.setUnknownTargetDeleted(unknownTargetNumber);
                    break;
                case ERROR:
                default:
                    throw new SyncTargetItemUnknownException(config.getItemType(), targetIds);
            }
        }
        // OK
        return result;
    }

}
