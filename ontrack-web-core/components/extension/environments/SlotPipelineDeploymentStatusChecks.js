import {Space, Table, Typography} from "antd";
import YesNo from "@components/common/YesNo";
import PopoverInfoIcon from "@components/common/PopoverInfoIcon";
import SlotAdmissionRuleCheck from "@components/extension/environments/SlotAdmissionRuleCheck";
import CheckIcon from "@components/common/CheckIcon";

const {Column} = Table

export default function SlotPipelineDeploymentStatusChecks({checks}) {
    return (
        <>
            <Table
                dataSource={checks}
                pagination={false}
                style={{width: '100%'}}
                title={() => <Typography.Title level={4}>Deployment checks</Typography.Title>}
            >

                <Column
                    key="status"
                    title="Deployable"
                    render={
                        (_, item) => <Space>
                            <CheckIcon value={item.check.status}/>
                            <YesNo value={item.check.status}/>
                            <PopoverInfoIcon
                                condition={item.check.reason}
                                title="Pipeline cannot be deployed"
                                content={
                                    <Typography.Text type="secondary">{item.check.reason}</Typography.Text>
                                }
                            />
                        </Space>
                    }
                />

                <Column
                    key="override"
                    title="Overridden"
                    render={
                        (_, item) => <Space>
                            <YesNo value={item.check.override?.override}/>
                            <PopoverInfoIcon
                                condition={item.override?.override === true}
                                title="This check has been overridden"
                                content={
                                    <Space direction="vertical">
                                        <Typography.Text
                                            type="secondary">{item.check.override?.overrideMessage}</Typography.Text>
                                        {/* TODO User & timestamp */}
                                        {/* TODO Override data? */}
                                    </Space>
                                }
                            />
                        </Space>
                    }
                />

                <Column
                    key="rule"
                    title="Rule check details"
                    render={
                        (_, item) => <SlotAdmissionRuleCheck
                            check={item.check.status}
                            ruleId={item.config.ruleId}
                            ruleConfig={item.config.ruleConfig}
                            ruleData={item.ruleData}
                        />
                    }
                />

            </Table>
        </>
    )
}