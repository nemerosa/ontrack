import {Space} from "antd";
import {branchLink} from "@components/common/Links";
import PromotionRunBox from "@components/promotionRuns/PromotionRunBox";
import LatestBuildBox from "@components/builds/LatestBuildBox";
import NoLatestBuildBox from "@components/builds/NoLatestBuildBox";

export default function BranchLastPromotionBox({branch}) {

    const lastPromotionLevel = branch.promotionLevels ? branch.promotionLevels[branch.promotionLevels.length - 1] : undefined
    const latestBuild = branch.latestBuild ? branch.latestBuild[0] : undefined

    return (
        <>
            <Space direction="vertical" size={4}>
                {
                    branchLink(branch)
                }
                {
                    lastPromotionLevel ?
                        <PromotionRunBox promotionLevel={lastPromotionLevel}></PromotionRunBox> : undefined
                }
                {
                    !lastPromotionLevel && latestBuild ? <LatestBuildBox build={latestBuild}/> : undefined
                }
                {
                    !lastPromotionLevel && !latestBuild ? <NoLatestBuildBox/> : undefined
                }
            </Space>
        </>
    )
}