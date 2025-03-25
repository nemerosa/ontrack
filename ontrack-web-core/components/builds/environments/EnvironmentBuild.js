import {Space, Typography} from "antd";
import {FaArrowLeft, FaArrowUp} from "react-icons/fa";
import BuildLink from "@components/builds/BuildLink";
import PromotionRuns from "@components/promotionRuns/PromotionRuns";

export default function EnvironmentBuild({slot, build, vertical = true}) {
    return (
        <>
            {
                slot.lastDeployedPipeline && slot.lastDeployedPipeline.build.id !== build.id &&
                <Space direction={vertical ? "vertical" : "horizontal"}>
                    <Typography.Text type="secondary">
                        {
                            vertical &&
                            <Space>
                                <FaArrowUp/>
                                Deployed
                            </Space>
                        }
                        {
                            !vertical &&
                            <FaArrowLeft/>
                        }
                    </Typography.Text>
                    <Space>
                        <BuildLink build={slot.lastDeployedPipeline.build}/>
                        <PromotionRuns
                            promotionRuns={slot.lastDeployedPipeline.build.promotionRuns}/>
                    </Space>
                </Space>
            }
        </>
    )
}