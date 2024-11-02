import FormDialog, {useFormDialog} from "@components/form/FormDialog";
import {Alert, Form, Input} from "antd";
import {gql} from "graphql-request";

export const useSlotPipelineOverrideRuleDialog = ({onSuccess}) => {
    return useFormDialog({
        prepareValues: (values, {pipeline, check}) => {
            return {
                pipelineId: pipeline.id,
                admissionRuleConfigId: check.config.id,
                message: values.message,
            }
        },
        query: gql`
            mutation OverridePipelineRule(
                $pipelineId: String!,
                $admissionRuleConfigId: String!,
                $message: String!,
            ) {
                overridePipelineRule(input: {
                    pipelineId: $pipelineId,
                    admissionRuleConfigId: $admissionRuleConfigId,
                    message: $message,
                }) {
                    errors {
                        message
                    }
                }
            }
        `,
        userNode: 'overridePipelineRule',
        onSuccess,
    })
}

export default function SlotPipelineOverrideRuleDialog({dialog}) {
    return (
        <>
            <FormDialog
                dialog={dialog}
            >
                <Form.Item>
                    <Alert
                        type="warning"
                        message="By overriding the rule, you may bypass some controls. This action will be logged."
                        showIcon={true}
                    />
                </Form.Item>
                <Form.Item
                    name="message"
                    label="Message"
                    extra="Justification message for the override"
                    rules={[
                        {
                            required: true,
                            message: 'Message is required.',
                        },
                    ]}
                >
                    <Input/>
                </Form.Item>
            </FormDialog>
        </>
    )
}