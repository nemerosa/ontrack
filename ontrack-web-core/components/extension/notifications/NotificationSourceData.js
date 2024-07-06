import {Dynamic} from "@components/common/Dynamic";

export default function NotificationSourceData({source}) {
    return (
        <>
            <Dynamic
                path={`framework/notification-source/${source.id}/Display`}
                props={{...source.data}}
            />
        </>
    )
}