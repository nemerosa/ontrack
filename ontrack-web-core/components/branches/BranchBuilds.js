import BuildBox from "@components/builds/BuildBox";
import {Button, Col, Popover, Row, Space, Spin, Table, Typography} from "antd";
import {FaSearch} from "react-icons/fa";
import PromotionRun from "@components/promotionRuns/PromotionRun";
import RangeSelector from "@components/common/RangeSelector";
import LegacyIndicator from "@components/common/LegacyIndicator";
import {useRouter} from "next/router";
import {gitChangeLogUri} from "@components/common/Links";
import BuildFilterDropdown from "@components/branches/filters/builds/BuildFilterDropdown";
import ValidationStampFilterDropdown from "@components/branches/filters/validationStamps/ValidationStampFilterDropdown";
import ValidationStampHeader from "@components/branches/ValidationStampHeader";
import ValidationRunCell from "@components/branches/ValidationRunCell";

const {Column} = Table;

/**
 *
 * @param builds List of builds to display
 * @param loadingBuilds Build loading indicator
 * @param pageInfo Page info for the builds
 * @param onLoadMore Callback when wanting to get more builds
 * @param rangeSelection Callback for the range selection
 * @param validationStamps List of validation stamps
 * @param loadingValidationStamps Validation stamps loading indicator
 * @param onChange Callback when builds, runs, statuses, etc. need to be reloaded after a user action
 */
export default function BranchBuilds({
                                         builds, loadingBuilds, pageInfo, onLoadMore, rangeSelection,
                                         validationStamps, loadingValidationStamps,
                                         onChange,
                                     }) {

    const router = useRouter()

    const onChangeLog = () => {
        if (rangeSelection.isComplete()) {
            const [from, to] = rangeSelection.selection
            // Legacy only for now
            // TODO This should be an extension
            // noinspection JSIgnoredPromiseFromCall
            router.push(gitChangeLogUri({from, to}))
        }
    }

    return (
        <>
            <Space className="ot-line" direction="vertical" size={8}>
                <Table
                    size="small"
                    dataSource={builds}
                    footer={() => (
                        <>
                            <Space>
                                <Popover
                                    content={
                                        (pageInfo && pageInfo.nextPage) ?
                                            "There are more builds to be loaded" :
                                            "There are no more builds to be loaded"
                                    }
                                >
                                    <Button
                                        onClick={onLoadMore}
                                        disabled={!pageInfo || !pageInfo.nextPage}
                                    >
                                        <Space>
                                            <FaSearch/>
                                            <Typography.Text>Load more...</Typography.Text>
                                        </Space>
                                    </Button>
                                </Popover>
                                {
                                    loadingBuilds &&
                                    <Space>
                                        <Spin/>
                                        <Typography.Text disabled>Loading builds...</Typography.Text>
                                    </Space>
                                }
                            </Space>
                        </>
                    )}
                    pagination={false} // Pagination is managed by the "load more"
                >
                    <Column
                        width="2em"
                        key="header"
                        colSpan={3}
                        align="left"
                        title={
                            <Space>
                                {/* Build filter */}
                                <BuildFilterDropdown
                                />
                                {/* Change log */}
                                <LegacyIndicator>
                                    <Popover
                                        title="Change log between two builds"
                                        content={
                                            (!rangeSelection || !rangeSelection.isComplete()) &&
                                            <Typography.Text disabled>
                                                Select two builds in order to get their change log
                                            </Typography.Text>
                                        }
                                    >
                                        <Button
                                            disabled={!rangeSelection || !rangeSelection.isComplete()}
                                            onClick={onChangeLog}
                                        >
                                            <Space>
                                                <Typography.Text>Change log</Typography.Text>
                                            </Space>
                                        </Button>
                                    </Popover>
                                </LegacyIndicator>
                                {/* VS filter */}
                                <ValidationStampFilterDropdown
                                />
                            </Space>
                        }
                        render={(_, build) =>
                            <RangeSelector
                                id={build.id}
                                title="Select this build as a boundary for a change log."
                                rangeSelection={rangeSelection}
                            />
                        }
                    />
                    <Column
                        key="build"
                        colSpan={0} // Header managed by the "header" column
                        render={(_, build) =>
                            <BuildBox build={build} displayDecorations={true}/>
                        }
                    />
                    <Column
                        key="promotions"
                        colSpan={0} // Header managed by the "header" column
                        render={(_, build) =>
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
                        }
                    />
                    {/* One column per validation stamp */}
                    {
                        validationStamps.map(validationStamp => (
                            <Column
                                key={validationStamp.name}
                                title={
                                    <ValidationStampHeader
                                        key={validationStamp.id}
                                        validationStamp={validationStamp}
                                    />
                                }
                                render={(_, build) =>
                                    <ValidationRunCell
                                        build={build}
                                        validationStamp={validationStamp}
                                        onChange={onChange}
                                    />
                                }
                            />
                        ))
                    }
                </Table>
            </Space>
        </>
    )
}