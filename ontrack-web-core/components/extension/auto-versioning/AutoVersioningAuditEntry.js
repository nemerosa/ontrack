import {Col, Descriptions, Popover, Row, Space, Timeline, Typography} from "antd";
import AutoVersioningAuditEntryTarget from "@components/extension/auto-versioning/AutoVersioningAuditEntryTarget";
import AutoVersioningAuditEntryState from "@components/extension/auto-versioning/AutoVersioningAuditEntryState";
import TimestampText from "@components/common/TimestampText";
import {FaInfoCircle} from "react-icons/fa";
import AutoVersioningAuditEntryStateData from "@components/extension/auto-versioning/AutoVersioningAuditEntryStateData";
import ProjectLink from "@components/projects/ProjectLink";
import ProjectLinkByName from "@components/projects/ProjectLinkByName";

export default function AutoVersioningAuditEntry({entry}) {

    const items = [
        {
            key: 'target',
            label: "Target",
            children: <AutoVersioningAuditEntryTarget entry={entry}/>,
        },
        {
            key: 'source',
            label: "Source",
            children: <ProjectLinkByName name={entry.order.sourceProject}/>,
        },
        {
            key: 'version',
            label: "Version",
            children: entry.order.targetVersion,
        },
        {
            key: 'post-processing',
            label: "Post processing",
            children: entry.order.postProcessing,
        },
        {
            key: 'post-processing-config',
            label: "Post processing config",
            children: <Typography.Paragraph code>
                {JSON.stringify(entry.order.postProcessingConfig)}
            </Typography.Paragraph>,
        },
        {
            key: 'routing',
            label: "Routing key",
            children: entry.routing,
        },
        {
            key: 'queue',
            label: "Queue name",
            children: entry.queue,
        },
        {
            key: 'auto-approval',
            label: "Auto approval",
            children: entry.order.autoApproval,
        },
        {
            key: 'auto-approval-mode',
            label: "Auto approval mode",
            children: entry.order.autoApprovalMode,
        },
        {
            key: 'targetPaths',
            label: "Target paths",
            children: <Space>
                {
                    entry.order.targetPaths.map((path, index) =>
                        <Typography.Text key={index} code>
                            {path}
                        </Typography.Text>
                    )
                }
            </Space>,
        },
        {
            key: 'targetPropertyType',
            label: "Target property type",
            children: <Typography.Text code>{entry.order.targetPropertyType}</Typography.Text>,
        },
        {
            key: 'targetProperty',
            label: "Target property",
            children: <Typography.Text code>{entry.order.targetProperty}</Typography.Text>,
        },
        {
            key: 'targetPropertyRegex',
            label: "Target property regex",
            children: <Typography.Text code>{entry.order.targetPropertyRegex}</Typography.Text>,
        },
        {
            key: 'targetRegex',
            label: "Target regex",
            children: <Typography.Text code>{entry.order.targetRegex}</Typography.Text>,
        },
        {
            key: 'upgradeBranchPattern',
            label: "Upgrade branch pattern",
            children: <Typography.Text code>{entry.order.upgradeBranchPattern}</Typography.Text>,
        },
        {
            key: 'validationStamp',
            label: "Validation stamp",
            children: <Typography.Text code>{entry.order.validationStamp}</Typography.Text>,
        },
    ]

    const history = entry.audit.map(item => (
        {
            children: <AutoVersioningAuditEntryState status={item}/>,
            label: <Space>
                <TimestampText value={item.creation.time}/>
                <Popover
                    content={
                        <AutoVersioningAuditEntryStateData data={item.data}/>
                    }
                >
                    <FaInfoCircle
                        color="lightblue"
                        className="ot-action"
                    />
                </Popover>
            </Space>,
        }
    ))

    return (
        <>
            <Row>
                <Col span={14}>
                    <Timeline
                        style={{
                            paddingTop: '3em',
                        }}
                        items={history}
                        mode="right"
                    />
                </Col>
                <Col span={10}>
                    <Descriptions
                        column={1}
                        size="small"
                        title={
                            <Space>
                                <Typography.Text>Auto versioning audit entry</Typography.Text>
                                <Typography.Text code>{entry.order.uuid}</Typography.Text>
                            </Space>
                        }
                        items={items}
                    />
                </Col>
            </Row>
        </>
    )
}