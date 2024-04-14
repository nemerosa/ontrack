import {Select} from "antd";
import NotificationResultType, {
    notificationResultTypes
} from "@components/extension/notifications/NotificationResultType";

export default function SelectNotificationResultType({value, onChange}) {

    const options = notificationResultTypes.map(it => ({
        value: it.key,
        label: <NotificationResultType type={it.key}/>,
    }))

    return (
        <>
            <Select
                options={options}
                value={value}
                onChange={onChange}
                style={{
                    width: '15em',
                }}
            />
        </>
    )
}