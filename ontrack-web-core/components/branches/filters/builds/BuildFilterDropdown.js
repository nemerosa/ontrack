import {useState} from "react";
import {Button, Dropdown, Space, Typography} from "antd";
import {FaCaretDown, FaFilter} from "react-icons/fa";

export default function BuildFilterDropdown() {

    const [items, setItems] = useState([])

    return (
        <>
            <Dropdown
                menu={{
                    items,
                }}
                trigger={['click']}
                className="ot-action"
            >
                <Button>
                    <Space>
                        <FaFilter/>
                        <Typography.Text>Build filter</Typography.Text>
                        <FaCaretDown/>
                    </Space>
                </Button>
            </Dropdown>
        </>
    )
}