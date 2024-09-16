import {Dynamic} from "@components/common/Dynamic";

export default function NotificationRecordResultLink({channel, result}) {
    return <Dynamic
        path={`framework/notification-channel/${channel}/ResultLink`}
        props={{...result?.output}}
    />
}