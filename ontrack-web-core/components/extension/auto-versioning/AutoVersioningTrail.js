import {Divider, Space, Table, Typography} from "antd";
import ProjectLink from "@components/projects/ProjectLink";
import BranchLink from "@components/branches/BranchLink";
import Link from "next/link";
import {autoVersioningAuditEntryUri, branchAutoVersioningUri} from "@components/common/Links";
import {FaBan, FaCog, FaMagic} from "react-icons/fa";
import CheckStatus from "@components/common/CheckStatus";
import AutoVersioningApproval from "@components/extension/auto-versioning/AutoVersioningApproval";
import AutoVersioningAuditEntryState from "@components/extension/auto-versioning/AutoVersioningAuditEntryState";
import AutoVersioningAuditEntryPR from "@components/extension/auto-versioning/AutoVersioningAuditEntryPR";

const {Column} = Table

export default function AutoVersioningTrail({trail}) {
    return (
        <>
            <Table
                dataSource={trail.branches}
                pagination={false}
            >

                <Column
                    key="branch"
                    title="Target branch"
                    render={(_, branchTrail) => (
                        <>
                            <Space>
                                <ProjectLink project={branchTrail.branch.project}/>/<BranchLink
                                branch={branchTrail.branch}/>
                                {
                                    branchTrail.configuration &&
                                    <Link href={branchAutoVersioningUri(branchTrail.branch)}
                                          title="Auto-versioning config"><FaCog/></Link>
                                }
                            </Space>
                        </>
                    )}
                />

                <Column
                    key="state"
                    title="State"
                    render={(_, branchTrail) => (
                        <>
                            <Space direction="vertical">
                                <CheckStatus
                                    value={!branchTrail.rejectionReason}
                                    text="Eligible"
                                    noText="Not eligible"
                                />
                                {
                                    branchTrail.rejectionReason &&
                                    <Typography.Text type="secondary">{branchTrail.rejectionReason}</Typography.Text>
                                }
                            </Space>
                        </>
                    )}
                />

                <Column
                    key="approval"
                    title="Approval"
                    render={(_, {configuration}) =>
                        <>
                            {
                                configuration &&
                                <AutoVersioningApproval
                                    autoApproval={configuration.autoApproval}
                                    autoApprovalMode={configuration.autoApprovalMode}
                                />
                            }
                        </>
                    }
                />

                <Column
                    key="targetPath"
                    title="Target path"
                    render={(_, {configuration}) =>
                        <>
                            {
                                configuration &&
                                <Typography.Text code>{configuration.targetPath}</Typography.Text>
                            }
                        </>
                    }
                />

                <Column
                    key="audit"
                    title="Audit"
                    render={(_, branchTrail) => (
                        <>
                            {
                                !branchTrail.orderId && <Space>
                                    <FaBan/>
                                    No AV process was scheduled
                                </Space>
                            }
                            {
                                branchTrail.orderId && <Space>
                                    <Link href={autoVersioningAuditEntryUri(branchTrail.orderId)}>
                                        <Space>
                                            <FaMagic/>
                                            Audit
                                        </Space>
                                    </Link>
                                    {
                                        branchTrail.audit?.mostRecentState &&
                                        <>
                                            <Divider type="vertical"/>
                                            <AutoVersioningAuditEntryPR
                                                entry={branchTrail.audit}
                                            />
                                            <Divider type="vertical"/>
                                            <AutoVersioningAuditEntryState
                                                status={branchTrail.audit.mostRecentState}
                                            />
                                        </>
                                    }
                                </Space>
                            }
                        </>
                    )}
                />

            </Table>
        </>
    )
}