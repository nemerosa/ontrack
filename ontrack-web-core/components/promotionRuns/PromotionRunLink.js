import Link from "next/link";
import {promotionRunUri} from "@components/common/Links";

export default function PromotionRunLink({promotionRun, text = "Run"}) {
    return (
        <>
            <Link href={promotionRunUri(promotionRun)}>{text}</Link>
        </>
    )
}