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
    return useFormDialog({
        ...config,
        init: (form, context) => {
            form.setFieldsValue({
                validationStamp: context.validationStamp?.name,
            })
        },
        prepareValues: (values, context) => {
            console.log({values})
            values.buildId = context.build.id
        },
        query: gql`
            #            mutation PromoteBuild($buildId: Int!, $promotion: String!, $description: String, $dateTime: LocalDateTime) {
            #                createPromotionRunById(input: {
            #                    buildId: $buildId,
            #                    promotion: $promotion,
            #                    description: $description,
            #                    dateTime: $dateTime,
            #                }) {
            #                    errors {
            #                        message
            #                    }
            #                }
            #            }
        `,
        userNode: 'createPromotionRunById',
    })
}

export default function BuildValidateDialog({buildValidateDialog}) {

    const [dataType, setDataType] = useState()

    const onValidationStampSelected = (vs) => {
        setDataType(vs.dataType)
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
                    dataType &&
                    <Form.Item
                        label="Validation data"
                    >
                        <Well>
                            <ValidationRunDataForm
                                dataType={dataType}
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