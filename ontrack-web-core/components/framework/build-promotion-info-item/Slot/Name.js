import {slotUri} from "@components/extension/environments/EnvironmentsLinksUtils";
import Link from "next/link";

export default function SlotBuildPromotionInfoItemName({item, build, onChange}) {
    return (
        <>
            <Link href={slotUri(item)}>
                {item.environment.name}{item.qualifier && `/${item.qualifier}`}
            </Link>
        </>
    )
}