import {Dynamic} from "@components/common/Dynamic";

export default function NotificationChannelConfig({channel, config}) {
    return <Dynamic
        path={`framework/notification-channel/${channel}/Config`}
        props={{...config}}
    />
}