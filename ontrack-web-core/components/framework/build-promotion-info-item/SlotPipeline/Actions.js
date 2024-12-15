import {environmentsUri} from "@components/extension/environments/EnvironmentsLinksUtils";
import Link from "next/link";
import TimestampText from "@components/common/TimestampText";
import React from "react";

export default function SlotPipelineBuildPromotionInfoItemActions({item, onChange}) {
    return (
        <>
            <Link href={environmentsUri}>
                <TimestampText value={item.end}/>
            </Link>
        </>
    )
}