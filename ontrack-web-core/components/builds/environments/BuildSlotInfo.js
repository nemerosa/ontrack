import Link from "next/link";
import {slotPipelineUri, slotUri} from "@components/extension/environments/EnvironmentsLinksUtils";
import {Button, Space, Typography} from "antd";
import {slotNameWithoutProject} from "@components/extension/environments/SlotName";
import {FaChevronCircleDown, FaChevronCircleRight} from "react-icons/fa";
import EnvironmentCurrentBuild from "@components/builds/environments/EnvironmentCurrentBuild";
import EnvironmentBuild from "@components/builds/environments/EnvironmentBuild";
import {isAuthorized} from "@components/common/authorizations";
import SlotPipelineCreateButton from "@components/extension/environments/SlotPipelineCreateButton";
import {buildKnownName} from "@components/common/Titles";
import {useRouter} from "next/router";

export default function BuildSlotInfo({slot, build, showDetails = false, setShowDetails, refresh}) {

    const router = useRouter()

    const toggleShowDetailsOn = () => {
        if (setShowDetails) setShowDetails(true)
    }

    const toggleShowDetailsOff = () => {
        if (setShowDetails) setShowDetails(false)
    }

    const navigateToPipeline = async (pipelineId) => {
        await router.push(slotPipelineUri(pipelineId))
    }

    return (
        <>
            <Space>
                <Link href={slotUri(slot)}>
                    <Typography.Text strong>
                        {slotNameWithoutProject(slot)}
                    </Typography.Text>
                </Link>
                {
                    !showDetails &&
                    <>
                        <EnvironmentBuild slot={slot} build={build} vertical={false}/>
                        <EnvironmentCurrentBuild slot={slot} build={build}/>
                        {
                            isAuthorized(slot, "pipeline", "create") &&
                            <SlotPipelineCreateButton
                                slot={slot}
                                build={build}
                                onStart={navigateToPipeline}
                                text={buildKnownName(build)}
                                title={`Start deploying ${buildKnownName(build)}`}
                            />
                        }
                        <Button
                            type="text"
                            icon={<FaChevronCircleRight/>}
                            title="Show deployment details"
                            onClick={toggleShowDetailsOn}
                        />
                    </>
                }
                {
                    showDetails &&
                    <Button
                        type="text"
                        icon={<FaChevronCircleDown/>}
                        title="Hide deployment details"
                        onClick={toggleShowDetailsOff}
                    />
                }
            </Space>
                {
                    showDetails &&
                    <>
                        <EnvironmentBuild slot={slot} build={build}/>
                        <EnvironmentCurrentBuild slot={slot} build={build}/>
                    </>
                }
        </>
    )
}