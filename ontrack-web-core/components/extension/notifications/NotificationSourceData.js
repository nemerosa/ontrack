import {Dynamic} from "@components/common/Dynamic";

export default function NotificationSourceData({source}) {
    return (
        <>
            {source &&
                <Dynamic
                    path={`framework/notification-source/${source.id}/Display`}
                    props={{...source.data}}
                />
            }
        </>
    )
}