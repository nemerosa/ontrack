import BuildBox from "@components/builds/BuildBox";
import {Button, Col, Row, Space, Typography} from "antd";
import {FaCheckSquare, FaSearch} from "react-icons/fa";
import PromotionRun from "@components/promotionRuns/PromotionRun";
import RangeSelector from "@components/common/RangeSelector";
import {useContext} from "react";
import {BranchViewContext} from "@components/branches/BranchViewContext";

export default function BranchBuilds({builds, pageInfo, onLoadMore}) {

    const {buildRange} = useContext(BranchViewContext)

    return (
        <>
            <Space className="ot-line" direction="vertical" size={8}>
                {
                    builds && builds.map(build =>
                        <Row
                            key={build.id}
                            gutter={8}
                            style={{
                                border: "solid 1px #ccc",
                                borderRadius: 8,
                                padding: 8,
                            }}
                        >
                            <Col>
                                <RangeSelector
                                    id={build.id}
                                    title="Select this build as a boundary for a change log."
                                    range={buildRange.range}
                                    onRangeChange={buildRange.onRangeChange}
                                />
                            </Col>
                            <Col span={4}>
                                <BuildBox build={build}/>
                            </Col>
                            <Col span={6}>
                                <Space size={8}>
                                    {
                                        build.promotionRuns.map(promotionRun =>
                                            <PromotionRun
                                                key={promotionRun.id}
                                                promotionRun={promotionRun}
                                                size={24}
                                            />
                                        )
                                    }
                                </Space>
                            </Col>
                        </Row>
                    )
                }
                {
                    pageInfo && pageInfo.nextPage && <Button onClick={onLoadMore}>
                        <Space>
                            <FaSearch/>
                            <Typography.Text>Load more...</Typography.Text>
                        </Space>
                    </Button>
                }
            </Space>
        </>
    )
}