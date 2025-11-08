import {Command} from "@components/common/Commands";
import {FaForwardStep} from "react-icons/fa6";
import {buildKnownName} from "@components/common/Titles";
import {buildUri} from "@components/common/Links";

export default function NextBuildCommand({nextBuild}) {
    return (
        <>
            {
                nextBuild && <Command
                    icon={<FaForwardStep/>}
                    text="Next build"
                    title={`Next build: ${buildKnownName(nextBuild)}`}
                    href={buildUri(nextBuild)}
                />
            }
        </>
    )
}