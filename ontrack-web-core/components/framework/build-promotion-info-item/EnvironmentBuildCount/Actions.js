import {Badge} from "antd";
import Link from "next/link";
import {environmentsUri} from "@components/extension/environments/EnvironmentsLinksUtils";

export default function EnvironmentBuildCountBuildPromotionInfoItemActions({item, build, onChange}) {
    return (
        <>
            {/* TODO https://trello.com/c/LYl0s5dH/151-environment-page-as-control-board */}
            <Link href={environmentsUri}>
                <Badge count={item.count} showZero color="green"/>
            </Link>
        </>
    )
}