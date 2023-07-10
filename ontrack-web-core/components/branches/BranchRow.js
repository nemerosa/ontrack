import {Divider, Tag} from "antd";
import BranchBox from "@components/branches/BranchBox";
import LatestBuildBox from "@components/builds/LatestBuildBox";
import NoLatestBuildBox from "@components/builds/NoLatestBuildBox";
import PromotionRunBox from "@components/promotionRuns/PromotionRunBox";
import RowTag from "@components/common/RowTag";

export default function BranchRow({branch, showProject}) {

    const latestBuild = branch.latestBuild ? branch.latestBuild[0] : undefined

    return (
        <>
            <RowTag><BranchBox branch={branch} showProject={showProject}/></RowTag>

            <Divider type="vertical"/>

            {
                latestBuild && <RowTag><LatestBuildBox build={latestBuild}/></RowTag>
            }
            {
                !latestBuild && <RowTag><NoLatestBuildBox/></RowTag>
            }
            {
                branch.promotionLevels && branch.promotionLevels.map(pl =>
                    <>
                        <RowTag><PromotionRunBox key={pl.id} promotionLevel={pl}/></RowTag>
                    </>
                )
            }
        </>
    )
}