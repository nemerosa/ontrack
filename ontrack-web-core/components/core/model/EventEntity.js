import {extractProjectEntityInfo} from "@components/entities/ProjectEntityPageInfo";
import {Space} from "antd";
import {useEffect, useState} from "react";

export default function EventEntity({event}) {

    const [info, setInfo] = useState(null)

    useEffect(() => {

        let entityType = null
        let entity = null

        if (event.entities.VALIDATION_RUN) {
            entityType = 'VALIDATION_RUN'
            entity = event.entities.VALIDATION_RUN
        } else if (event.entities.PROMOTION_RUN) {
            entityType = 'PROMOTION_RUN'
            entity = event.entities.PROMOTION_RUN
        } else if (event.entities.PROMOTION_LEVEL) {
            entityType = 'PROMOTION_LEVEL'
            entity = event.entities.PROMOTION_LEVEL
        } else if (event.entities.VALIDATION_STAMP) {
            entityType = 'VALIDATION_STAMP'
            entity = event.entities.VALIDATION_STAMP
        } else if (event.entities.BUILD) {
            entityType = 'BUILD'
            entity = event.entities.BUILD
        } else if (event.entities.BRANCH) {
            entityType = 'BRANCH'
            entity = event.entities.BRANCH
        } else if (event.entities.PROJECT) {
            entityType = 'PROJECT'
            entity = event.entities.PROJECT
        }

        setInfo(extractProjectEntityInfo(entityType, entity))
    }, [event])


    return (
        <>
            <Space>
                {info?.type}
                {info?.component}
            </Space>
        </>
    )
}