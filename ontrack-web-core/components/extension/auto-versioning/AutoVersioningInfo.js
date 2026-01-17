import {Space, Spin, Tooltip, Typography} from "antd";
import {FaCalendar, FaInfoCircle, FaMedal, FaTag, FaThumbsUp} from "react-icons/fa";
import BuildRef from "@components/links/BuildRef";
import Rows from "@components/common/Rows";
import Columns from "@components/common/Columns";
import Timestamp from "@components/common/Timestamp";
import Link from "next/link";
import CheckStatus from "@components/common/CheckStatus";
import AutoVersioningPRLink from "@components/extension/auto-versioning/AutoVersioningPRLink";
import AutoVersioningAuditEntryState from "@components/extension/auto-versioning/AutoVersioningAuditEntryState";

export default function AutoVersioningInfo({autoVersioning, branchLink}) {
    const commonStatusText = "Indicates if the auto versioning has caught up with last eligible build or not."
    return (
        <>
            <Space direction="vertical" className="ot-line">
                {/* Last eligible build */}
                {
                    autoVersioning.lastEligibleBuild &&
                    <Space>
                        <FaMedal title="Last eligible build"/>
                        <BuildRef build={autoVersioning.lastEligibleBuild}/>
                        <CheckStatus
                            value={branchLink.targetBuild.id === autoVersioning.lastEligibleBuild.id}
                            tooltip={(status) => status ?
                                `Using latest. ${commonStatusText}` :
                                `Not using latest. ${commonStatusText}`
                            }
                        />
                    </Space>
                }
                {/* AV status */}
                {
                    autoVersioning.status &&
                    <Space>
                        {/* Running indicator */}
                        {
                            autoVersioning.status.running ?
                                <Spin size="small"/> :
                                <FaThumbsUp/>
                        }
                        {/* Most recent state */}
                        <Tooltip
                            color="#eee"
                            title={
                                <Rows>
                                    <Columns>
                                        <FaTag color="black"/>
                                        <Typography.Text>Version</Typography.Text>
                                        <Typography.Text
                                            strong>{autoVersioning.status.order.targetVersion}</Typography.Text>
                                    </Columns>
                                    <Columns>
                                        <FaCalendar color="black"/>
                                        <Typography.Text>Created at</Typography.Text>
                                        <Typography.Text strong>
                                            <Timestamp value={autoVersioning.status.mostRecentState.creation.time}/>
                                        </Typography.Text>
                                    </Columns>
                                    <AutoVersioningPRLink
                                        autoVersioningStatusMostRecentStateData={autoVersioning.status.mostRecentState.data}/>
                                    <Columns>
                                        <FaInfoCircle color="blue"/>
                                        <Link
                                            href={`/extension/auto-versioning/audit/detail/${autoVersioning.status.order.uuid}`}>More
                                            info...</Link>
                                    </Columns>
                                </Rows>
                            }
                        >
                            <Space>
                                <AutoVersioningAuditEntryState status={autoVersioning.status.mostRecentState} displayTooltip={false}/>
                            </Space>
                        </Tooltip>
                    </Space>
                }
                {
                    autoVersioning.status &&
                    <AutoVersioningPRLink
                        autoVersioningStatusMostRecentStateData={autoVersioning.status.mostRecentState.data}
                        size={8}
                    />
                }
            </Space>
        </>
    )
}