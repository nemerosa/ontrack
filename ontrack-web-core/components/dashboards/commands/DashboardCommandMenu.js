import {Button, Dropdown, Space, Typography} from "antd";
import {Command} from "@components/common/Commands";
import {FaEdit, FaWindowRestore} from "react-icons/fa";
import Link from "next/link";
import {useContext} from "react";
import {DashboardContext} from "@components/dashboards/DashboardPage";

export default function DashboardCommandMenu() {

    const dashboard = useContext(DashboardContext)

    const editDashboard = () => {
        // TODO
    }

    const items = [
        {
            key: 'edit',
            label: (
                <Command
                    icon={<FaEdit/>}
                    text="Edit current"
                    action={editDashboard}
                />
            ),
        },
    ]

    return (
        <>
            <Dropdown menu={{items}}>
                <Button
                    type="text"
                >
                    <Space size={8}>
                        <FaWindowRestore/>
                        <Typography.Text>Dashboard</Typography.Text>
                    </Space>
                </Button>
            </Dropdown>
        </>
    )
}