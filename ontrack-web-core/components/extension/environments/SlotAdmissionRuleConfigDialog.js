import FormDialog, {useFormDialog} from "@components/form/FormDialog";
import {Form, Input} from "antd";
import SelectSlotAdmissionRule from "@components/extension/environments/SelectSlotAdmissionRule";
import Well from "@components/common/Well";
import {useState} from "react";
import SlotAdmissionRuleForm from "@components/extension/environments/SlotAdmissionRuleForm";
import {gql} from "graphql-request";

export const useSlotAdmissionRuleConfigDialog = ({onSuccess}) => {

    const [currentValues, setCurrentValues] = useState({})

    return useFormDialog({
        onSuccess: onSuccess,
        init: (form, context) => {
            setCurrentValues({...context})
            // form.setFieldValue('id', context.id)
            form.setFieldValue('name', context.name)
            form.setFieldValue('description', context.description)
            form.setFieldValue('ruleId', context.ruleId)
            form.setFieldValue('ruleConfig', context.ruleConfig)
        },
        currentValues,
        setCurrentValues,
        query: gql`
            mutation SaveAdmissionRuleConfig(
                $slotId: String!,
                $name: String,
                $description: String!,
                $ruleId: String!,
                $ruleConfig: JSON!,
            ) {
                saveSlotAdmissionRuleConfig(input: {
                    slotId: $slotId,
                    name: $name,
                    description: $description,
                    ruleId: $ruleId,
                    ruleConfig: $ruleConfig,
                }) {
                    errors {
                        message
                    }
                }
            }
        `,
        userNode: 'saveSlotAdmissionRuleConfig',
        prepareValues: (values, context) => {
            return {
                ...values,
                description: values.description ?? '',
                slotId: context.slot.id,
            }
        },
    })
}

export default function SlotAdmissionRuleConfigDialog({dialog}) {

    const onValuesChange = (changedValues /*, allValues*/) => {
        if (changedValues.hasOwnProperty('ruleId')) {
            const ruleId = changedValues.ruleId
            if (ruleId !== dialog.currentValues.ruleId) {
                dialog.form.setFieldValue('ruleConfig', null)
                dialog.setCurrentValues({...dialog.currentValues, ruleId, ruleConfig: null})
            }
        }
    }

    return (
        <>
            <FormDialog dialog={dialog} onValuesChange={onValuesChange}>
                <Form.Item
                    name="name"
                    label="Name"
                >
                    <Input/>
                </Form.Item>
                <Form.Item
                    name="description"
                    label="Description"
                >
                    <Input/>
                </Form.Item>
                <Form.Item
                    name="ruleId"
                    label="Rule configuration"
                    rules={[
                        {
                            required: true,
                            message: 'Rule is required',
                        },
                    ]}
                >
                    <SelectSlotAdmissionRule/>
                </Form.Item>
                {
                    dialog.currentValues.ruleId &&
                    <Form.Item
                        name="ruleConfig"
                        label="Configuration"
                    >
                        <Well>
                            <SlotAdmissionRuleForm
                                ruleId={dialog.currentValues.ruleId}
                            />
                        </Well>
                    </Form.Item>
                }
            </FormDialog>
        </>
    )
}