import {useEffect, useState} from "react";
import {Button, Dropdown, Space, Typography} from "antd";
import {FaCogs} from "react-icons/fa";
import UserMenuItemLink from "@components/layouts/UserMenuItemLink";
import {groupIcons} from "@components/layouts/UserMenu";

export default function UserMenuActions({actions}) {

    const [items, setItems] = useState([])

    useEffect(() => {
        if (actions) {
            setItems(
                actions.map(action => ({
                    key: `${action.extension}-${action.id}`,
                    icon: groupIcons[action.groupId],
                    label: <UserMenuItemLink item={action}/>,
                }))
            )
        }
    }, [actions])

    return (
        <>
            {
                items.length > 0 &&
                <Dropdown menu={{items}}>
                    <Button type="text">
                        <Space size={8}>
                            <FaCogs/>
                            <Typography.Text>Tools</Typography.Text>
                        </Space>
                    </Button>
                </Dropdown>
            }
        </>
    )
}