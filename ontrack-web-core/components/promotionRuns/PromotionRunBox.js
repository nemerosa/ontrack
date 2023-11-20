import TransitionBox from "@components/common/TransitionBox";
import {buildLink, promotionLevelLink} from "@components/common/Links";
import {FaBan} from "react-icons/fa";

export default function PromotionRunBox({promotionLevel}) {
    const run = promotionLevel.promotionRuns ? promotionLevel.promotionRuns[0] : undefined
    const build = run?.build
    return (
        <>
            <TransitionBox
                before={promotionLevelLink(promotionLevel)}
                after={
                    build ? buildLink(build) : <FaBan/>
                }
            />
        </>
    )
}