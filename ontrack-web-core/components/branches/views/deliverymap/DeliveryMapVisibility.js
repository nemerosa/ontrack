import {Checkbox, Space, Tooltip, Typography} from "antd";
import {isShown, visibilityKinds} from "@components/branches/views/deliverymap/visibilityKinds";

/**
 * The toolbar control saying what is drawn on the map.
 *
 * One labelled checkbox per entry of `visibilityKinds`, in the view's header rather than in the
 * graph's control bar: what is ON the map is not the same kind of question as how it is drawn, and a
 * row of unlabelled eye icons would not survive the second entry - see `visibilityKinds` for why
 * there will be more than one.
 *
 * Icons AND text: the icon is what makes the row scannable once there are several, and the text is
 * what makes any of them mean anything the first time.
 *
 * @param visibility The reader's preferences, keyed by kind id
 * @param onToggle Called with the kind id and whether it is now shown
 */
export default function DeliveryMapVisibility({visibility, onToggle}) {
    return (
        <Space size="middle" data-testid="delivery-map-visibility">
            <Typography.Text type="secondary">Show</Typography.Text>
            {
                // The tooltip wraps the checkbox rather than being a `title` on it: antd puts any
                // extra property it is given on the INPUT, so a title there would only appear while
                // hovering the box itself and never over the label beside it
                visibilityKinds.map(kind => (
                    <Tooltip key={kind.id} title={kind.title}>
                        <Checkbox
                            checked={isShown(visibility, kind.id)}
                            onChange={event => onToggle(kind.id, event.target.checked)}
                            data-testid={`delivery-map-show-${kind.id}`}
                        >
                            <Space size={4}>
                                {kind.icon}
                                {kind.label}
                            </Space>
                        </Checkbox>
                    </Tooltip>
                ))
            }
        </Space>
    )
}
