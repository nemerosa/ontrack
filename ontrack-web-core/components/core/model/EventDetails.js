import EventDisplay from "@components/core/model/EventDisplay";
import {Space} from "antd";
import {extractProjectEntityInfo} from "@components/entities/ProjectEntityPageInfo";
import Link from "next/link";

export default function EventDetails({event}) {
    return (
        <>
            <Space direction="vertical">
                <EventDisplay event={event.eventType.id}/>
                {
                    Object.keys(event.entities).map((entityType) => {
                        const entity = event.entities[entityType]
                        const info = extractProjectEntityInfo(entityType, entity)
                        if (info) {
                            return (
                                <>
                                    <Space>
                                        {info.type}
                                        <Link href={info.href}>{info.name}</Link>
                                    </Space>
                                </>
                            )
                        }
                    })
                }
            </Space>
        </>
    )
}