import {Space, Typography} from "antd";
import {FaBan} from "react-icons/fa";
import SlotLink from "@components/extension/environments/SlotLink";
import BuildLink from "@components/builds/BuildLink";
import PromotionRuns from "@components/promotionRuns/PromotionRuns";

export default function EnvironmentQualifiedProject({environment, qualifiedProject}) {

    const slot = environment.slots.find(it => it.project.name === qualifiedProject.project.name && it.qualifier === qualifiedProject.qualifier)

    return (
        <>
            {
                !slot && <Typography.Text type="secondary"><FaBan/></Typography.Text>
            }
            {
                slot && <>
                    <Space direction="vertical">
                        <Space>
                            {
                                slot.lastDeployedPipeline && <>
                                    {/*<SlotPipelineLink*/}
                                    {/*    pipelineId={slot.lastDeployedPipeline.id}*/}
                                    {/*    numberOnly={true}*/}
                                    {/*/>*/}
                                    <BuildLink build={slot.lastDeployedPipeline.build}/>
                                </>
                            }
                            <SlotLink slot={slot}/>
                        </Space>
                        {
                            slot.lastDeployedPipeline &&
                            <PromotionRuns promotionRuns={slot.lastDeployedPipeline.build.promotionRuns}/>
                        }
                    </Space>
                </>
            }
        </>
    )
}