import Link from "next/link";
import {environmentsUri} from "@components/extension/environments/EnvironmentsLinksUtils";

export default function SlotPipelineBuildPromotionInfoItemActions({item, build, promotionLevel, onChange}) {
    return (
        <>
            {/* TODO https://trello.com/c/LYl0s5dH/151-environment-page-as-control-board */}
            <Link href={environmentsUri}>
                {item.slot.environment.name}{item.slot.qualifier && `/${item.slot.qualifier}`}&nbsp;&nbsp;
            </Link>
        </>
    )
}