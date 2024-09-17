import GridCell from "@components/grid/GridCell";
import React from "react";
import NotificationRecordingsTable from "@components/extension/notifications/NotificationRecordingsTable";

export default function BuildContentNotifications({build}) {
    return (
        <>
            <GridCell id="notifications" title="Notifications" padding={false}>
                <NotificationRecordingsTable
                    entity={{
                        type: 'BUILD',
                        id: build.id,
                    }}
                    sourceId="entity-subscription"
                />
            </GridCell>
        </>
    )
}