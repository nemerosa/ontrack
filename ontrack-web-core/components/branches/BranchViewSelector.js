import {FaCaretDown, FaWindowRestore} from "react-icons/fa";
import {Dropdown, Space, Typography} from "antd";
import {useEffect, useState} from "react";

export default function BranchViewSelector({branchViews, initialSelectedViewKey, onBranchViewSelected}) {

    const [selectedViewKey, setSelectedViewKey] = useState(initialSelectedViewKey)

    const [items, setItems] = useState([])
    useEffect(() => {
        setItems(branchViews.map(branchView => ({
            ...branchView,
            onClick: () => {
                setSelectedViewKey(branchView.key)
                onBranchViewSelected(branchView)
            }
        })))
    }, [branchViews]);

    return (
        <>
            <Dropdown
                menu={{
                    selectedKeys: [selectedViewKey],
                    items,
                }}
                trigger={['click']}
                className="ot-action"
            >
                <Space>
                    <FaWindowRestore/>
                    <Typography.Text>View</Typography.Text>
                    <FaCaretDown/>
                </Space>
            </Dropdown>
        </>
    )
}