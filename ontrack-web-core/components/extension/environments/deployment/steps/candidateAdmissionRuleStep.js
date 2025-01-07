import {Space} from "antd";
import SlotAdmissionRuleSummary from "@components/extension/environments/SlotAdmissionRuleSummary";
import {FaTasks} from "react-icons/fa";
import CheckIcon from "@components/common/CheckIcon";

export const candidateAdmissionRuleStep = (deployment, rule) => {
    return {
        title: <Space>
            {/* Status of the rule */}
            <CheckIcon value={rule.check.ok}/>
            {/* TODO Input */}
            {/* TODO Overriding */}
            {/* Name of the rule */}
            <SlotAdmissionRuleSummary
                ruleId={rule.admissionRuleConfig.ruleId}
                ruleConfig={rule.admissionRuleConfig.ruleConfig}
            />
        </Space>,
        description: '',
        icon: <FaTasks/>,
    }
}
