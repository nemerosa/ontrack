import {Space} from "antd";
import CheckIcon from "@components/common/CheckIcon";
import {Dynamic} from "@components/common/Dynamic";

export default function SlotAdmissionRuleCheck({check, ruleId, ruleConfig, ruleData}) {
    return (
        <>
            <Space>
                <CheckIcon
                    value={check}
                />
                <Dynamic
                    path={`framework/environments-slot-admission-rule/${ruleId}/Check`}
                    props={{check, ruleConfig, ruleData}}
                />
            </Space>
        </>
    )
}