import FormDialog, {useFormDialog} from "@components/form/FormDialog";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {getValidationStampById, gqlValidationStampFragment} from "@components/services/fragments";
import {Form, Input} from "antd";
import {gql} from "graphql-request";
import {EventsContext} from "@components/common/EventsContext";
import {useContext, useState} from "react";
import SelectValidationDataType from "@components/validationStamps/SelectValidationDataType";
import Well from "@components/common/Well";
import ValidationDataTypeForm from "@components/framework/validation-data-type-form/ValidationDataTypeForm";

export const useValidationStampUpdateDialog = () => {

    const client = useGraphQLClient()
    const eventsContext = useContext(EventsContext)

    const [dataType, setDataType] = useState()

    return useFormDialog({
        dataType, setDataType,
        init: (form, {id}) => {
            getValidationStampById(client, id).then(vs => {
                setDataType(vs.dataType)
                return form.setFieldsValue({
                    ...vs,
                    dataType: vs.dataType.descriptor.id,
                });
            })
        },
        prepareValues: (values, {id}) => {
            console.log({values, id, dataType})
            return {
                ...values,
                id,
                dataTypeId: values.dataType,
                dataTypeConfig: values.config,
            }
        },
        query: gql`
            mutation UpdateValidationStamp(
                $id: Int!,
                $name: String!,
                $description: String!,
                $dataTypeId: String,
                $dataTypeConfig: JSON,
            ) {
                updateValidationStampById(input: {
                    id: $id,
                    name: $name,
                    description: $description,
                    dataType: $dataTypeId,
                    dataTypeConfig: $dataTypeConfig,
                }) {
                    errors {
                        message
                    }
                    validationStamp {
                        ...ValidationStampData
                    }
                }
            }
            ${gqlValidationStampFragment}
        `,
        userNode: 'updateValidationStampById',
        onSuccess: (updateValidationStampById) => {
            eventsContext.fireEvent("validationStamp.updated", {...updateValidationStampById.validationStamp})
        }
    })
}

export default function ValidationStampUpdateDialog({validationStampUpdateDialog}) {

    const onValidationDataTypeSelected = (dataTypeId) => {
        validationStampUpdateDialog.setDataType(previous => ({
            ...previous,
            descriptor: {
                id: dataTypeId,
            }
        }))
    }

    return (
        <>
            <FormDialog dialog={validationStampUpdateDialog}>
                <Form.Item
                    name="name"
                    label="Name"
                    rules={[{required: true, message: 'Name is required.'}]}
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
                    validationStampUpdateDialog.dataType &&
                    <Form.Item
                        label="Validation data"
                    >
                        <Well>
                            <ValidationDataTypeForm
                                prefix="config"
                                dataType={validationStampUpdateDialog.dataType}
                            />
                        </Well>
                    </Form.Item>
                }
            </FormDialog>
        </>
    )
}