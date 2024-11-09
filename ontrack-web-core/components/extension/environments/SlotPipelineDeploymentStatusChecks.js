import {Space, Table, Typography} from "antd";
import YesNo from "@components/common/YesNo";
import PopoverInfoIcon from "@components/common/PopoverInfoIcon";
import SlotAdmissionRuleCheck from "@components/extension/environments/SlotAdmissionRuleCheck";
import CheckIcon from "@components/common/CheckIcon";
import SlotPipelineOverrideRuleButton from "@components/extension/environments/SlotPipelineOverrideRuleButton";
import {FaExclamationTriangle} from "react-icons/fa";
import {useQuery} from "@components/services/useQuery";
import {gql} from "graphql-request";

const {Column} = Table

export default function SlotPipelineDeploymentStatusChecks({pipeline, onChange}) {

    const {loading, error, data} = useQuery(
        gql`
            query PipelineChecks($pipelineId: String!) {
                slotPipelineById(id: $pipelineId) {
                    deploymentStatus {
                        checks {
                            canBeOverridden
                            check {
                                status
                                reason
                            }
                            override {
                                override
                                overrideMessage
                                user
                                timestamp
                                data
                            }
                            config {
                                id
                                name
                                ruleId
                                ruleConfig
                            }
                            ruleData
                        }
                    }
                }
            }
        `,
        {variables: {pipelineId: pipeline.id}},
    )

    return (
        <>
            <Table
                loading={loading}
                dataSource={data?.slotPipelineById?.deploymentStatus?.checks}
                pagination={false}
                style={{width: '100%'}}
            >

                <Column
                    key="status"
                    title="Deployable"
                    render={
                        (_, item) => <Space>
                            <CheckIcon value={item.check.status}/>
                            <YesNo id={`deployable-${item.config.id}`} value={item.check.status}/>
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
                            <YesNo id={`overridden-${item.config.name}`} value={item.override?.override}/>
                            <SlotPipelineOverrideRuleButton
                                pipeline={pipeline}
                                check={item}
                                onChange={onChange}
                            />
                            <PopoverInfoIcon
                                condition={item.override?.override === true}
                                icon={<FaExclamationTriangle color="orange"/>}
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
                            configId={item.config.id}
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