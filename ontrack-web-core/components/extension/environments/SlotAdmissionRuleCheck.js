import {Space} from "antd";
import CheckIcon from "@components/common/CheckIcon";
import {Dynamic} from "@components/common/Dynamic";

export default function SlotAdmissionRuleCheck({check, configId, ruleId, ruleConfig, ruleData}) {
    return (
        <>
            <Space data-testid={`details-${configId}`}>
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