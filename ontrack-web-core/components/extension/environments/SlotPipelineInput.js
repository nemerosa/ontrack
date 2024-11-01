import {Card} from "antd";
import SlotAdmissionRuleDataForm from "@components/extension/environments/SlotAdmissionRuleDataForm";

export default function SlotPipelineInput({input}) {
    return (
        <>
            <Card
                size="small"
                title={input.config.name}
            >
                <SlotAdmissionRuleDataForm
                    configId={input.config.id}
                    ruleId={input.config.ruleId}
                    ruleConfig={input.config.ruleConfig}
                />
            </Card>
        </>
    )
}