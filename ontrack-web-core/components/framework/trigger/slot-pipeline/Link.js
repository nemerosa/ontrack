import {FaServer} from "react-icons/fa";
import Link from "next/link";
import {slotPipelineUri} from "@components/extension/environments/EnvironmentsLinksUtils";
import {Popover, Space} from "antd";
import SlotPipelineLink from "@components/extension/environments/SlotPipelineLink";
import SlotPipelineStatusLabel from "@components/extension/environments/SlotPipelineStatusLabel";

export default function SlotPipelineTriggerLink({pipelineId, status}) {
    return (
        <>
            <Popover
                title={
                    <SlotPipelineLink pipelineId={pipelineId}/>
                }
                content={
                    <Space>
                        Triggered on
                        <SlotPipelineStatusLabel status={status}/>
                    </Space>
                }
            >
                <Link href={slotPipelineUri(pipelineId)}>
                    <FaServer/>
                </Link>
            </Popover>
        </>
    )
}