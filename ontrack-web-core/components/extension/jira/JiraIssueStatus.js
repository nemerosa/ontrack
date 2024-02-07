import {Space, Typography} from "antd";
import Image from "next/image";

export default function JiraIssueStatus({status}) {
    return (
        <>
            <Space>
                <img src={status.iconUrl} alt={`${status.name} icon`} width={16} height={16}/>
                <Typography.Text>{status.name}</Typography.Text>
            </Space>
        </>
    )
}