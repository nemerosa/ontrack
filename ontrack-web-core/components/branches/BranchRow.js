import {Col, Row} from "antd";
import BranchBox from "@components/branches/BranchBox";
import LatestBuildBox from "@components/builds/LatestBuildBox";
import NoLatestBuildBox from "@components/builds/NoLatestBuildBox";
import PromotionRunBox from "@components/promotionRuns/PromotionRunBox";
import RowTag from "@components/common/RowTag";

export default function BranchRow({branch, showProject}) {

    const latestBuild = branch.latestBuild ? branch.latestBuild[0] : undefined

    return (
        <>
            <Row gutter={[8, 8]} wrap={true}>
                <Col flex="none">
                    <RowTag><BranchBox branch={branch} showProject={showProject}/></RowTag>
                </Col>
                <Col>
                    <Row wrap={true} gutter={[8, 8]}>
                        <Col>
                            {
                                latestBuild && <RowTag><LatestBuildBox build={latestBuild}/></RowTag>
                            }
                            {
                                !latestBuild && <RowTag><NoLatestBuildBox/></RowTag>
                            }
                        </Col>
                        {
                            branch.promotionLevels && branch.promotionLevels.map(pl =>
                                <Col key={pl.id}>
                                    <RowTag><PromotionRunBox promotionLevel={pl}/></RowTag>
                                </Col>
                            )
                        }
                    </Row>
                </Col>
            </Row>
        </>
    )
}