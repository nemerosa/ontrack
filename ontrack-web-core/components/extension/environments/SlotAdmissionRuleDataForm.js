import {Dynamic} from "@components/common/Dynamic";

export default function SlotAdmissionRuleDataForm({configId, ruleId, ruleConfig}) {
    return <Dynamic
        path={`framework/environments-slot-admission-rule/${ruleId}/DataForm`}
        props={{configId, ...ruleConfig}}
    />
}