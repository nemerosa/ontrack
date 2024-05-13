import {Dynamic} from "@components/common/Dynamic";

export default function NotificationRecordOutput({channel, output}) {
    return <Dynamic
        path={`framework/notification-channel/${channel}/Output`}
        props={{...output}}
    />
}