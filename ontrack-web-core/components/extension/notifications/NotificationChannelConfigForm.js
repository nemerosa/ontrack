import {Dynamic} from "@components/common/Dynamic";

export default function NotificationChannelConfigForm({prefix, channelType}) {
    return <Dynamic
        path={`framework/notification-channel-form/${channelType}`}
        props={{prefix}}
    />
}