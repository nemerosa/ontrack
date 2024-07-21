import {Tag} from "antd";

export default function AutoVersioningConfigNotificationScope({scopes}) {
    return (
        <>
            {
                scopes.map((scope, index) => (
                    <Tag key={index}>{scope}</Tag>
                ))
            }
        </>
    )
}