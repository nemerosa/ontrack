import {Space, Tag} from "antd";
import {useContext} from "react";
import {DashboardContext} from "@components/dashboards/DashboardContextProvider";

export default function DashboardPageTitle({title}) {

    const {dashboard} = useContext(DashboardContext)

    return (
        <Space>
            {title}
            <Tag>
                {dashboard?.name}
            </Tag>
        </Space>
    )
}