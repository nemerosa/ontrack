import Link from "next/link";
import {slotUri} from "@components/extension/environments/EnvironmentsLinksUtils";
import {Space, Typography} from "antd";
import {slotNameWithoutProject} from "@components/extension/environments/SlotName";
import {FaArrowUp, FaStar} from "react-icons/fa";
import BuildLink from "@components/builds/BuildLink";
import PromotionRuns from "@components/promotionRuns/PromotionRuns";

export default function BuildSlotInfo({slot, build}) {
    return (
        <>
            <Link href={slotUri(slot)}>
                <Typography.Text strong>
                    {slotNameWithoutProject(slot)}
                </Typography.Text>
            </Link>
            {
                slot.lastDeployedPipeline && slot.lastDeployedPipeline.build.id !== build.id &&
                <Space direction="vertical">
                    <Typography.Text type="secondary">
                        <Space>
                            <FaArrowUp/>
                            Deployed
                        </Space>
                    </Typography.Text>
                    <Space>
                        <BuildLink build={slot.lastDeployedPipeline.build}/>
                        <PromotionRuns
                            promotionRuns={slot.lastDeployedPipeline.build.promotionRuns}/>
                    </Space>
                </Space>
            }
            {
                slot.lastDeployedPipeline && slot.lastDeployedPipeline.build.id === build.id &&
                <Space
                    style={{
                        backgroundColor: "lightyellow",
                        padding: '0.5em',
                        borderRadius: '0.5em',
                    }}
                >
                    <FaStar color="green"/>
                    <Typography.Text>Current build</Typography.Text>
                </Space>
            }
        </>
    )
}