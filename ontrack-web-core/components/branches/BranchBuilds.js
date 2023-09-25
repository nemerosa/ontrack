import BuildBox from "@components/builds/BuildBox";
import {Button, Col, Popover, Row, Space, Spin, Table, Typography} from "antd";
import {FaCheckSquare, FaEyeSlash, FaSearch, FaSquare} from "react-icons/fa";
import PromotionRun from "@components/promotionRuns/PromotionRun";
import RangeSelector from "@components/common/RangeSelector";
import LegacyIndicator from "@components/common/LegacyIndicator";
import {useRouter} from "next/router";
import {gitChangeLogUri} from "@components/common/Links";
import BuildFilterDropdown from "@components/branches/filters/builds/BuildFilterDropdown";
import ValidationStampFilterDropdown from "@components/branches/filters/validationStamps/ValidationStampFilterDropdown";
import ValidationStampHeader from "@components/branches/ValidationStampHeader";
import ValidationRunCell from "@components/branches/ValidationRunCell";
import {useState} from "react";
import ValidationGroups from "@components/validationRuns/ValidationGroups";
import {usePreferences} from "@components/providers/PreferencesProvider";

const {Column} = Table;

/**
 * @param branch Target branch
 * @param builds List of builds to display
 * @param loadingBuilds Build loading indicator
 * @param pageInfo Page info for the builds
 * @param onLoadMore Callback when wanting to get more builds
 * @param rangeSelection Callback for the range selection
 * @param validationStamps List of validation stamps
 * @param loadingValidationStamps Validation stamps loading indicator
 * @param onChange Callback when builds, runs, statuses, etc. need to be reloaded after a user action
 * @param selectedBuildFilter Currently selected build filter (can be undefined)
 */
export default function BranchBuilds({
                                         branch, builds, loadingBuilds, pageInfo, onLoadMore, rangeSelection,
                                         validationStamps, loadingValidationStamps,
                                         onChange,
                                         selectedBuildFilter, onSelectedBuildFilter, onPermalinkBuildFilter,
                                         selectedValidationStampFilter, onSelectedValidationStampFilter,
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

    // Preferences for the display (filters, options...)

    const {branchViewVsGroups, setPreferences} = usePreferences()

    const [grouping, setGrouping] = useState(branchViewVsGroups)
    const onGroupingChange = () => {
        const newGrouping = !grouping
        setPreferences({branchViewVsGroups: newGrouping})
        setGrouping(newGrouping)
    }

    // Online edition of the validation stamp filter
    const [inlineEdition, setInlineEdition] = useState(false)

    const onInlineEdition = (flag) => {
        setInlineEdition(flag)
    }

    const stopInlineEdition = () => {
        onInlineEdition(false)
    }

    return (
        <>
            <Space className="ot-line" direction="vertical" size={8}>
                <Table
                    className={
                        inlineEdition ? "ot-validation-stamp-filter-edition" : undefined
                    }
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
                            </Space>
                        </>
                    )}
                    pagination={false} // Pagination is managed by the "load more"
                    title={() => {
                        return inlineEdition && selectedValidationStampFilter ?
                            <Row>
                                <Col span={24} align="right">
                                    <Space>
                                        <Button className="ot-validation-stamp-filter-edition">
                                            <Space>
                                                <FaCheckSquare/>
                                                <Typography.Text>
                                                    Select all for&nbsp;
                                                    <Typography.Text strong>{selectedValidationStampFilter.name}</Typography.Text>
                                                </Typography.Text>
                                            </Space>
                                        </Button>
                                        <Button className="ot-validation-stamp-filter-edition">
                                            <Space>
                                                <FaSquare/>
                                                <Typography.Text>
                                                    Select none for&nbsp;
                                                    <Typography.Text strong>{selectedValidationStampFilter.name}</Typography.Text>
                                                </Typography.Text>
                                            </Space>
                                        </Button>
                                        <Button className="ot-validation-stamp-filter-edition" onClick={stopInlineEdition}>
                                            <Space>
                                                <FaEyeSlash/>
                                                <Typography.Text>
                                                    <Typography.Text strong>{selectedValidationStampFilter.name}</Typography.Text>
                                                    &nbsp;done editing
                                                </Typography.Text>
                                            </Space>
                                        </Button>
                                    </Space>
                                </Col>
                            </Row> : undefined
                    }}
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
                                    branch={branch}
                                    selectedBuildFilter={selectedBuildFilter}
                                    onSelectedBuildFilter={onSelectedBuildFilter}
                                    onPermalink={onPermalinkBuildFilter}
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
                                    branch={branch}
                                    grouping={grouping}
                                    onGroupingChange={onGroupingChange}
                                    selectedValidationStampFilter={selectedValidationStampFilter}
                                    onSelectedValidationStampFilter={onSelectedValidationStampFilter}
                                    inlineEdition={inlineEdition}
                                    onInlineEdition={onInlineEdition}
                                />
                                {
                                    loadingBuilds &&
                                    <Popover content="Loading builds...">
                                        <Spin/>
                                    </Popover>
                                }
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
                        validationStamps
                            .filter(validationStamp => {
                                // If we are in inline edition mode, we display ALL the validation stamps
                                if (inlineEdition) {
                                    return true
                                }
                                // If a filter is selected, we use only this filter
                                else if (selectedValidationStampFilter) {
                                    // Checking the filter
                                    return selectedValidationStampFilter.vsNames.indexOf(validationStamp.name) >= 0
                                }
                                    // If grouping is selected, we keep only the validation stamps which are
                                // part of the current filter
                                else {
                                    return !grouping
                                }
                            })
                            .map(validationStamp => (
                                <Column
                                    key={validationStamp.name}
                                    className="ot-validation-stamp"
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
                    {/* Grouping per validation status */}
                    {
                        !inlineEdition && grouping && <Column
                            key="groups"
                            render={(_, build) =>
                                <ValidationGroups build={build}/>
                            }
                        />
                    }
                </Table>
            </Space>
        </>
    )
}