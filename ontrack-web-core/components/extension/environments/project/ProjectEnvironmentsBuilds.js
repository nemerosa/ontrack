import {Card, Flex, Space} from "antd";
import {
    useProjectEnvironmentsContext
} from "@components/extension/environments/project/ProjectEnvironmentsContextProvider";
import LoadingContainer from "@components/common/LoadingContainer";
import EnvironmentIcon from "@components/extension/environments/EnvironmentIcon";
import BuildLink from "@components/builds/BuildLink";
import PromotionRuns from "@components/promotionRuns/PromotionRuns";
import SlotBuildEligibilitySwitch from "@components/extension/environments/SlotBuildEligibilitySwitch";

export default function ProjectEnvironmentsBuilds() {

    const {
        loadingBuilds,
        builds,
        selectedBuild,
        setSelectedBuild,
        selectedSlot,
        allEligible,
        setAllEligible,
    } = useProjectEnvironmentsContext()

    return (
        <>
            <Space
                direction="vertical"
                style={{
                    width: '100%',
                }}
            >
                {
                    selectedSlot &&
                    <SlotBuildEligibilitySwitch value={allEligible} onChange={setAllEligible}/>
                }
                <LoadingContainer loading={loadingBuilds}
                >
                    <Flex
                        vertical={true}
                        justify="flex-start"
                        alignItems="flex-start"
                        gap="small"
                        style={{
                            width: '100%',
                        }}
                    >
                        {
                            builds.map(build => (
                                <Card
                                    key={build.id}
                                    size="small"
                                    hoverable={true}
                                    onClick={() => setSelectedBuild(build)}
                                    style={{
                                        width: '100%',
                                        border: build.id === selectedBuild?.id ? "solid 3px black" : undefined,
                                    }}
                                >
                                    <Flex
                                        justify="flex-start"
                                        alignItems="flex-start"
                                        gap="small"
                                    >
                                        <Flex
                                            justify="flex-start"
                                            alignItems="flex-start"
                                            gap="small"
                                            vertical={true}
                                        >
                                            {
                                                build.deployed.map(pipeline => (
                                                    <>
                                                        <EnvironmentIcon
                                                            environmentId={pipeline.slot.environment.id}
                                                            tooltipText={`Deployed in ${pipeline.slot.environment.name}`}
                                                        />
                                                    </>
                                                ))
                                            }
                                        </Flex>
                                        <Flex
                                            justify="flex-start"
                                            alignItems="flex-start"
                                            gap="small"
                                            vertical={true}
                                        >
                                            <BuildLink build={build}/>
                                            <PromotionRuns promotionRuns={build.promotionRuns}/>
                                        </Flex>
                                    </Flex>
                                </Card>
                            ))
                        }
                    </Flex>
                </LoadingContainer>
            </Space>
        </>
    )
}