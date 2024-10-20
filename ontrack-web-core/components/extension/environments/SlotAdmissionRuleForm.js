import {Dynamic} from "@components/common/Dynamic";

export default function SlotAdmissionRuleForm({ruleId}) {
    return <Dynamic
        path={`framework/environments-slot-admission-rule/${ruleId}/Form`}
    />
}