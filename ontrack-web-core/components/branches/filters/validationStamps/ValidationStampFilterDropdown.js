import {Button, Dropdown, Space, Typography} from "antd";
import {FaCaretDown, FaFilter} from "react-icons/fa";
import {useEffect, useState} from "react";
import OldSelectableMenuItem from "@components/common/OldSelectableMenuItem";

/**
 * @param initialGrouping Initial value for the grouping preference
 * @param onGroupingChange Callback when the grouping preference changes
 */
export default function ValidationStampFilterDropdown({
                                                          initialGrouping,
                                                          onGroupingChange,
                                                      }) {

    const [items, setItems] = useState([])

    useEffect(() => {
        const menu = []
        // TODO Loading existing validation stamp filters
        // Separator
        menu.push({type: 'divider'})
        // TODO Displaying the VS names
        // Grouping the VS
        menu.push({
            key: 'group',
            label: <OldSelectableMenuItem
                text="Group validation stamps per status"
                initialSelectedValue={initialGrouping}
                onChange={onGroupingChange}
            />,
        })
        // OK
        setItems(menu)
    }, []);

    return (
        <>
            <Dropdown
                menu={{
                    // selectedKeys: [selectedViewKey],
                    items,
                }}
                trigger={['click']}
                className="ot-action"
            >
                <Button>
                    <Space>
                        <FaFilter/>
                        <Typography.Text>Validation filter</Typography.Text>
                        <FaCaretDown/>
                    </Space>
                </Button>
            </Dropdown>
        </>
    )
}