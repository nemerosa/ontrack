package net.nemerosa.ontrack.model.structure;

import lombok.Data;
import net.nemerosa.ontrack.model.exceptions.SyncTargetItemPresentException;
import net.nemerosa.ontrack.model.exceptions.SyncTargetItemUnknownException;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Behaviour to adopt when synchronising two lists.
 *
 * @deprecated Will be removed in V6. Use syncForward instead
 */
@Data
@Deprecated
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

    /**
     * This Sync policy is conservative, making sure existing items in the target are kept as they were.
     */
    public static final SyncPolicy COPY = new SyncPolicy(TargetPresentPolicy.IGNORE, UnknownTargetPolicy.IGNORE);

    /**
     * This Sync policy makes sure the source and the target are exactly aligned. Any existing item in the target
     * which is not in the source will be deleted.
     */
    public static final SyncPolicy SYNC = new SyncPolicy(TargetPresentPolicy.REPLACE, UnknownTargetPolicy.DELETE);

    /**
     * This Sync policy makes sure the source and the target common elements are synchronised. Any existing item in the
     * target is kept if not existing in the source.
     */
    public static final SyncPolicy SYNC_KEEP = new SyncPolicy(TargetPresentPolicy.REPLACE, UnknownTargetPolicy.IGNORE);

    public static enum TargetPresentPolicy {
        IGNORE,
        REPLACE,
        ERROR
    }

    public enum UnknownTargetPolicy {
        IGNORE,
        DELETE,
        ERROR
    }

    public <T, D> SyncResult sync(SyncConfig<T, D> config) {
        SyncResult result = SyncResult.empty();
        // Gets the source items
        Collection<T> sourceItems = config.getSourceItems();
        // Index of target items
        Map<D, T> targetMap = config.getTargetItems().stream().collect(
                Collectors.toMap(
                        config::getItemId,
                        Function.identity()
                )
        );
        // For each source item
        for (T sourceItem : sourceItems) {
            // Gets its identifier for the target items
            D itemId = config.getItemId(sourceItem);
            // Gets a corresponding item
            T targetItem = targetMap.get(itemId);
            // If present
            if (targetItem != null && config.isTargetItemPresent(targetItem)) {
                // This depends on the policy
                switch (targetPresentPolicy) {
                    case IGNORE:
                        result.ignorePresentTarget();
                        break;
                    case REPLACE:
                        config.replaceTargetItem(sourceItem, targetItem);
                        result.replacePresentTarget();
                        break;
                    case ERROR:
                    default:
                        throw new SyncTargetItemPresentException(config.getItemType(), itemId);
                }
            }
            // If not present, creates it
            else {
                config.createTargetItem(sourceItem);
                result.create();
            }
        }
        // Now that target items have been either created or updated, we need to know which ones were
        // not matched with the sources
        targetMap = config.getTargetItems().stream().collect(
                Collectors.toMap(
                        config::getItemId,
                        Function.identity()
                )
        );
        Collection<D> targetIds = new HashSet<>(targetMap.keySet());
        Collection<D> sourceIds = sourceItems.stream().map(config::getItemId).collect(Collectors.toList());
        targetIds.removeAll(sourceIds);
        int unknownTargetNumber = targetIds.size();
        if (unknownTargetNumber > 0) {
            switch (unknownTargetPolicy) {
                case IGNORE:
                    result.setUnknownTargetIgnored(unknownTargetNumber);
                    break;
                case DELETE:
                    for (D targetId : targetIds) {
                        T targetItem = targetMap.get(targetId);
                        config.deleteTargetItem(targetItem);
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
