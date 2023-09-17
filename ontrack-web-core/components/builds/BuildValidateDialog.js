import FormDialog, {useFormDialog} from "@components/form/FormDialog";
import {Form, Input} from "antd";
import {gql} from "graphql-request";
import SelectValidationStamp from "@components/validationStamps/SelectValidationStamp";

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
            values.buildId = context.build.id
            values.validationStamp = values.validationStamp
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
                    />
                </Form.Item>
                {/* TODO Validation data (depends on the validation stamp) */}
                {/* TODO Status */}
                {/* TODO Description */}
            </FormDialog>
        </>
    )
}