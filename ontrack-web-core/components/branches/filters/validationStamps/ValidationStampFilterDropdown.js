import {Button, Dropdown, Space, Typography} from "antd";
import {FaCaretDown, FaCheck, FaFilter} from "react-icons/fa";
import {useEffect, useState} from "react";

export default function ValidationStampFilterDropdown() {

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
            label: "Group validation stamps per status",
            icon: <FaCheck/>,
            // TODO Activate/deactivate the grouping
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