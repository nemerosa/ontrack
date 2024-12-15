import {environmentsUri} from "@components/extension/environments/EnvironmentsLinksUtils";
import Link from "next/link";

export default function EnvironmentBuildCountBuildPromotionInfoItemName({item, build, onChange}) {
    return (
        <>
            <Link
                href={environmentsUri}
                title="Number of environments this build is deployed to"
            >
                # of environments
            </Link>
        </>
    )
}