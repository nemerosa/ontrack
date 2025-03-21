import {Flex} from "antd";
import EnvironmentIcon from "@components/extension/environments/EnvironmentIcon";
import BuildSlotInfo from "@components/builds/environments/BuildSlotInfo";
import BuildEnvironmentDeployment from "@components/builds/environments/BuildEnvironmentDeployment";
import {useState} from "react";

export default function BuildEnvironment({slot, build, refresh}) {

    const [showDetails, setShowDetails] = useState(false)

    return (
        <>
            <Flex gap={16}>
                <Flex vertical={true} justify="flex-start">
                    <EnvironmentIcon environmentId={slot.environment.id}/>
                </Flex>
                <Flex vertical={true} justify="flex-start" align="flex-start" gap={8} flex={1}>
                    <BuildSlotInfo
                        slot={slot}
                        build={build}
                        showDetails={showDetails}
                        setShowDetails={setShowDetails}
                        refresh={refresh}/>
                </Flex>
                {
                    showDetails &&
                    <BuildEnvironmentDeployment slot={slot} build={build} refresh={refresh}/>
                }
            </Flex>
        </>
    )
}