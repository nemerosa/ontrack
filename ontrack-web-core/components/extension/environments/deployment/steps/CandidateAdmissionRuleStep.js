import {Space} from "antd";
import {FaTasks} from "react-icons/fa";
import {DeploymentStep} from "@components/extension/environments/deployment/steps/deploymentStatusSteps";
import CheckIcon from "@components/common/CheckIcon";
import SlotAdmissionRuleSummary from "@components/extension/environments/SlotAdmissionRuleSummary";
import SlotPipelineOverrideRuleButton from "@components/extension/environments/SlotPipelineOverrideRuleButton";
import SlotPipelineOverrideIndicator from "@components/extension/environments/SlotPipelineOverrideIndicator";
import SlotPipelineInputRuleButton from "@components/extension/environments/SlotPipelineInputRuleButton";

export function CandidateAdmissionRuleStep({pipeline, rule, disabled, onChange}) {
    return (
        <>
            <DeploymentStep
                id={`pipeline-rule-${rule.admissionRuleConfig.id}`}
                avatar={<FaTasks/>}
                title={
                    <Space>
                        <CheckIcon value={rule.check.ok}/>
                        {/* Input */}
                        {
                            !disabled &&
                            <SlotPipelineInputRuleButton
                                pipelineId={pipeline.id}
                                ruleConfigId={rule.admissionRuleConfig.id}
                                onChange={onChange}
                            />
                        }
                        {/* Overriding */}
                        {
                            !disabled &&
                            <SlotPipelineOverrideRuleButton
                                pipeline={pipeline}
                                rule={rule}
                                onChange={onChange}
                            />
                        }
                        {/* Overridden */}
                        {
                            <SlotPipelineOverrideIndicator
                                rule={rule}
                            />
                        }
                        {/* Name of the rule */}
                        <SlotAdmissionRuleSummary
                            ruleId={rule.admissionRuleConfig.ruleId}
                            ruleConfig={rule.admissionRuleConfig.ruleConfig}
                        />
                    </Space>
                }
            />
        </>
    )
}
