import {Flex, List, Typography} from "antd";
import {isAuthorized} from "@components/common/authorizations";
import SlotPipelineCreateButton from "@components/extension/environments/SlotPipelineCreateButton";
import BuildDeploymentListItem from "@components/builds/environments/BuildDeploymentListItem";
import {buildKnownName} from "@components/common/Titles";

export default function BuildEnvironmentDeployment({slot, build, refresh}) {
    return (
        <>
            <Flex vertical={true} justify="flex-start" gap={8} flex={3}>
                {
                    isAuthorized(slot, "pipeline", "create") &&
                    <SlotPipelineCreateButton
                        slot={slot}
                        build={build}
                        onStart={refresh}
                        text={`Start deploying ${buildKnownName(build)}`}
                    />
                }
                {
                    slot.currentPipeline && !slot.currentPipeline.finished && slot.currentPipeline.build.id !== build.id &&
                    <>
                        <Typography.Text type="secondary">
                            Another build is being deployed
                        </Typography.Text>
                        <BuildDeploymentListItem
                            deployment={slot.currentPipeline}
                            build={slot.currentPipeline.build}
                            refresh={refresh}
                        />
                    </>
                }
                {
                    slot.pipelines.pageItems.length === 0 &&
                    <Typography.Text type="secondary">This build was not deployed
                        yet</Typography.Text>
                }
                {
                    slot.pipelines.pageItems.length > 0 &&
                    <>
                        <Typography.Text type="secondary">Deployments for this
                            build</Typography.Text>
                        <List
                            size="small"
                            dataSource={slot.pipelines.pageItems}
                            renderItem={(deployment) =>
                                <BuildDeploymentListItem
                                    deployment={deployment}
                                    refresh={refresh}
                                />
                            }
                        />
                    </>
                }
            </Flex>
        </>
    )
}