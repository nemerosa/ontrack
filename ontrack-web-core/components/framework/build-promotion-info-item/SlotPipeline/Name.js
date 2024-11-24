import {slotPipelineUri} from "@components/extension/environments/EnvironmentsLinksUtils";
import Link from "next/link";

export default function SlotPipelineBuildPromotionInfoItemActions({item, build, promotionLevel, onChange}) {
    return (
        <>
            <Link href={slotPipelineUri(item.id)}>
                {item.slot.environment.name}{item.slot.qualifier && `/${item.slot.qualifier}`} #{item.number}
            </Link>
        </>
    )
}