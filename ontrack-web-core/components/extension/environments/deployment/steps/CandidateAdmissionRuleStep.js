import {Space} from "antd";
import {FaTasks} from "react-icons/fa";
import {DeploymentStep} from "@components/extension/environments/deployment/steps/deploymentStatusSteps";
import CheckIcon from "@components/common/CheckIcon";
import SlotAdmissionRuleSummary from "@components/extension/environments/SlotAdmissionRuleSummary";

export function CandidateAdmissionRuleStep({rule}) {
    return (
        <>
            <DeploymentStep
                avatar={<FaTasks/>}
                title={
                    <Space>
                        <CheckIcon value={rule.check.ok}/>
                        {/* TODO Input */}
                        {/* TODO Overriding */}
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
