import {Command} from "@components/common/Commands";
import {FaForwardStep} from "react-icons/fa6";
import {buildKnownName} from "@components/common/Titles";
import {buildUri} from "@components/common/Links";
import {useRouter} from "next/router";

export default function NextBuildCommand({nextBuild}) {
    const router = useRouter()

    return (
        <>
            {
                nextBuild && <Command
                    icon={<FaForwardStep/>}
                    text="Next build"
                    title={`Next build: ${buildKnownName(nextBuild)}`}
                    action={() => router.push(buildUri(nextBuild))}
                />
            }
        </>
    )
}