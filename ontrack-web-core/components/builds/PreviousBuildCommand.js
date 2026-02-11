import {Command} from "@components/common/Commands";
import {FaBackwardStep} from "react-icons/fa6";
import {buildKnownName} from "@components/common/Titles";
import {buildUri} from "@components/common/Links";
import {useRouter} from "next/router";

export default function PreviousBuildCommand({previousBuild}) {
    const router = useRouter()

    return (
        <>
            {
                previousBuild && <Command
                    icon={<FaBackwardStep/>}
                    text="Previous build"
                    title={`Previous build: ${buildKnownName(previousBuild)}`}
                    action={() => router.push(buildUri(previousBuild))}
                />
            }
        </>
    )
}