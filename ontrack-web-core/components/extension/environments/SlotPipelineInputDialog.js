import FormDialog, {useFormDialog} from "@components/form/FormDialog";
import LoadingContainer from "@components/common/LoadingContainer";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useState} from "react";
import {gql} from "graphql-request";
import {Space} from "antd";
import SlotPipelineInput from "@components/extension/environments/SlotPipelineInput";

export const useSlotPipelineInputDialog = () => {

    const client = useGraphQLClient()
    const [loading, setLoading] = useState(true)
    const [inputs, setInputs] = useState([])

    return useFormDialog({
        loading,
        inputs,
        init: (form, {pipeline}) => {
            setLoading(true)
            client.request(
                gql`
                    query PipelineInput($id: String!) {
                        slotPipelineById(id: $id) {
                            requiredInputs {
                                config {
                                    name
                                }
                                fields {
                                    name
                                    type
                                    label
                                    value
                                }
                            }
                        }
                    }
                `,
                {id: pipeline.id}
            ).then(data => {
                const inputs = data.slotPipelineById.requiredInputs;
                setInputs(inputs)
                inputs.forEach(input => {
                    input.fields.forEach(field => {
                        form.setFieldValue(
                            [input.config.name, field.name],
                            field.value
                        )
                    })
                })
            }).finally(() => {
                setLoading(false)
            })
        },
        prepareValues: (values, {pipeline}) => {
            const inputs = []
            Object.keys(values).forEach(ruleName => {
                const input = {
                    name: ruleName,
                    values: [],
                }
                inputs.push(input)
                const ruleValues = values[ruleName]
                Object.keys(ruleValues).forEach(ruleValueKey => {
                    const ruleValue = ruleValues[ruleValueKey]
                    input.values.push({
                        name: ruleValueKey,
                        value: JSON.stringify(ruleValue),
                    })
                })
            })
            return {
                pipelineId: pipeline.id,
                inputs: inputs,
            }
        },
        query: gql`
            mutation PipelineInput(
                $pipelineId: String!,
                $inputs: [SlotPipelineDataInput!]!,
            ) {
                updatePipelineData(input: {
                    pipelineId: $pipelineId,
                    inputs: $inputs,
                }) {
                    errors {
                        message
                    }
                }
            }
        `,
        userNode: 'updatePipelineData',
        onSuccess: (result, {onChange}) => {
            if (onChange) onChange()
        }
    })
}

export default function SlotPipelineInputDialog({dialog}) {
    return (
        <>
            <FormDialog dialog={dialog}>
                <LoadingContainer loading={dialog.loading}>
                    <Space direction="vertical" className="ot-line">
                        {
                            dialog.inputs.map((input, index) => (
                                <SlotPipelineInput
                                    key={index}
                                    input={input}
                                />
                            ))
                        }
                    </Space>
                </LoadingContainer>
            </FormDialog>
        </>
    )
}