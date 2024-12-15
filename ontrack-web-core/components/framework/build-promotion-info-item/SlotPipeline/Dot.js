import EnvironmentIcon from "@components/extension/environments/EnvironmentIcon";
import {environmentsUri, slotPipelineUri} from "@components/extension/environments/EnvironmentsLinksUtils";
import Link from "next/link";
import {Popover, Space} from "antd";
import TimestampText from "@components/common/TimestampText";
import React from "react";

export default function SlotPipelineBuildPromotionInfoItemDot({item}) {
    return (
        <>
            <Popover
                title={
                    <Space>
                        <EnvironmentIcon environmentId={item.slot.environment.id} showTooltip={false}/>
                        {item.slot.environment.name}
                    </Space>
                }
                content={
                    <>
                        <Space direction="vertical">
                            <Link href={slotPipelineUri(item.id)}>
                                <TimestampText value={item.end}/>
                            </Link>
                        </Space>
                    </>
                }
            >
                {/* TODO https://trello.com/c/LYl0s5dH/151-environment-page-as-control-board */}
                <Link href={environmentsUri}>
                    <EnvironmentIcon environmentId={item.slot.environment.id} showTooltip={false}/>
                </Link>
            </Popover>
        </>
    )
}