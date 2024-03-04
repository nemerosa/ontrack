import FormDialog, {useFormDialog} from "@components/form/FormDialog";
import {Form, Input} from "antd";
import {gql} from "graphql-request";
import SelectValidationStamp from "@components/validationStamps/SelectValidationStamp";
import SelectValidationRunStatus from "@components/validationRuns/SelectValidationRunStatus";
import Well from "@components/common/Well";
import {useState} from "react";
import ValidationRunDataForm from "@components/framework/validation-run-data-form/ValidationRunDataForm";

const {TextArea} = Input;

export function useBuildValidateDialog(config) {

    const [dataType, setDataType] = useState()

    return useFormDialog({
        ...config,
        dataType, setDataType,
        init: (form, context) => {
            setDataType(context.validationStamp.dataType)
            form.setFieldsValue({
                validationStamp: context.validationStamp?.name,
            })
        },
        prepareValues: (values, context) => {
            return {
                ...values,
                buildId: context.build.id,
                dataTypeId: dataType?.descriptor?.id,
            }
        },
        query: gql`
            mutation ValidateBuild(
                $buildId: Int!,
                $description: String,
                $validationStamp: String!,
                $status: String,
                $dataTypeId: String,
                $data: JSON,
            ) {
                createValidationRunById(input: {
                    buildId: $buildId,
                    description: $description,
                    validationStamp: $validationStamp,
                    dataTypeId: $dataTypeId,
                    data: $data,
                    validationRunStatus: $status,
                }) {
                    errors {
                        message
                    }
                }
            }
        `,
        userNode: 'createValidationRunById',
    })
}

export default function BuildValidateDialog({buildValidateDialog}) {


    const onValidationStampSelected = (vs) => {
        buildValidateDialog.setDataType(vs.dataType)
    }

    return (
        <>
            <FormDialog dialog={buildValidateDialog}>
                <Form.Item
                    name="validationStamp"
                    label="Validation stamp to promote to"
                    rules={[
                        {
                            required: true,
                            message: 'Validation stamp is required.',
                        },
                    ]}
                >
                    <SelectValidationStamp
                        branch={buildValidateDialog?.context?.build?.branch}
                        useName={true}
                        onValidationStampSelected={onValidationStampSelected}
                    />
                </Form.Item>
                {/* Validation data (depends on the validation stamp) */}
                {
                    buildValidateDialog.dataType &&
                    <Form.Item
                        label="Validation data"
                    >
                        <Well>
                            <ValidationRunDataForm
                                dataType={buildValidateDialog.dataType}
                            />
                        </Well>
                    </Form.Item>
                }
                {/* Status */}
                <Form.Item
                    name="status"
                    label="Status"
                >
                    <SelectValidationRunStatus/>
                </Form.Item>
                {/* Description */}
                <Form.Item
                    name="description"
                    label="Description"
                >
                    <TextArea rows={4}/>
                </Form.Item>
            </FormDialog>
        </>
    )
}