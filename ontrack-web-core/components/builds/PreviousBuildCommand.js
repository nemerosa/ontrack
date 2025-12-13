import {Command} from "@components/common/Commands";
import {FaBackwardStep} from "react-icons/fa6";
import {buildKnownName} from "@components/common/Titles";
import {buildUri} from "@components/common/Links";

export default function PreviousBuildCommand({previousBuild}) {
    return (
        <>
            {
                previousBuild && <Command
                    icon={<FaBackwardStep/>}
                    text="Previous build"
                    title={`Previous build: ${buildKnownName(previousBuild)}`}
                    href={buildUri(previousBuild)}
                />
            }
        </>
    )
}