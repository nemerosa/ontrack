import {FaWindowRestore} from "react-icons/fa";
import {Button, Dropdown, Space, Typography} from "antd";

/**
 * Selector for the change log view, rendered in the command bar of the change log page.
 *
 * The semantic view's own options are not here: they are controls in that view's own cell.
 * A view offering controls for itself is fine; what `docs/adr/0001-branch-content-views.md`
 * rules out is a view offering a selector for other views.
 *
 * @param views List of available views ({key, name, icon})
 * @param selectedViewKey Key of the view currently displayed
 * @param onSelect Called with the key of the view the user picks
 */
export default function ChangeLogViewSelector({views, selectedViewKey, onSelect}) {

    const items = views.map(view => ({
        key: view.key,
        label: view.name,
        icon: view.icon,
        onClick: () => onSelect(view.key),
    }))

    return (
        <>
            {
                items.length > 0 &&
                <Dropdown
                    menu={{
                        selectedKeys: [selectedViewKey],
                        items,
                        'data-testid': 'change-log-views',
                    }}
                    trigger={['click']}
                >
                    <Button type="text" title="Selection of the way to read this change log">
                        <Space size={8}>
                            <FaWindowRestore/>
                            <Typography.Text>View</Typography.Text>
                        </Space>
                    </Button>
                </Dropdown>
            }
        </>
    )
}
