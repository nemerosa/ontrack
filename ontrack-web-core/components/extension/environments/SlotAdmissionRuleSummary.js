import {Dynamic} from "@components/common/Dynamic";

export default function SlotAdmissionRuleSummary({ruleId, ruleConfig}) {
    return <Dynamic
        path={`framework/environments-slot-admission-rule/${ruleId}/Summary`}
        props={{...ruleConfig}}
    />
}