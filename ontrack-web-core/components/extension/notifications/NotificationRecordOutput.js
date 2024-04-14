import {Dynamic} from "@components/common/Dynamic";

export default function NotificationRecordOutput({channel, output}) {
    return <Dynamic
        path={`framework/notification-channel-output/${channel}`}
        props={{...output}}
    />
}