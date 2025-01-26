import FormDialog, {useFormDialog} from "@components/form/FormDialog";
import {Form, Input} from "antd";
import {gql} from "graphql-request";
import SelectValidationDataType from "@components/validationStamps/SelectValidationDataType";
import Well from "@components/common/Well";
import ValidationDataTypeForm from "@components/framework/validation-data-type-form/ValidationDataTypeForm";
import {useState} from "react";

export const usePredefinedValidationStampCreateDialog = ({onChange}) => {

    const [dataType, setDataType] = useState()

    return useFormDialog({
        onSuccess: onChange,
        dataType, setDataType,
        prepareValues: (values) => {
            return {
                ...values,
                description: values.description ?? '',
                dataType: values.dataType,
                dataTypeConfig: values.config,
            }
        },
        query: gql`
            mutation CreatePredefinedValidationStamp(
                $name: String!,
                $description: String!,
                $dataType: String,
                $dataTypeConfig: JSON,
            ) {
                createPredefinedValidationStamp(input: {
                    name: $name,
                    description: $description,
                    dataType: $dataType,
                    dataTypeConfig: $dataTypeConfig,
                }) {
                    errors {
                        message
                    }
                }
            }
        `,
        userNode: 'createPredefinedValidationStamp',
    })
}

export default function PredefinedValidationStampCreateDialog({dialog}) {

    const onValidationDataTypeSelected = (dataTypeId) => {
        dialog.setDataType(previous => ({
            ...previous,
            descriptor: {
                id: dataTypeId,
            }
        }))
    }

    return (
        <>
            <FormDialog dialog={dialog}>
                <Form.Item
                    name="name"
                    label="Name"
                    rules={[
                        {required: true, message: "Name is required"},
                    ]}
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
                    name="dataType"
                    label="Data type"
                >
                    <SelectValidationDataType onValidationDataTypeSelected={onValidationDataTypeSelected}/>
                </Form.Item>
                {/* Validation data config (depends on the selected data type) */}
                {
                    dialog.dataType &&
                    <Form.Item
                        label="Validation data"
                    >
                        <Well>
                            <ValidationDataTypeForm
                                prefix="config"
                                dataType={dialog.dataType}
                            />
                        </Well>
                    </Form.Item>
                }
            </FormDialog>
        </>
    )
}
