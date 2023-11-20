import FormDialog, {useFormDialog} from "@components/form/FormDialog";
import {DatePicker, Form, Input} from "antd";
import SelectPromotionLevel from "@components/promotionLevels/SelectPromotionLevel";
import dayjs from "dayjs";
import {gql} from "graphql-request";

const {TextArea} = Input;

export function useBuildPromoteDialog(config) {
    return useFormDialog({
        ...config,
        init: (form, context) => {
            form.setFieldsValue({
                promotionLevel: context.promotionLevel?.name,
                dateTime: dayjs(),
            })
        },
        prepareValues: (values, context) => {
            values.buildId = context.build.id
            values.promotion = values.promotionLevel
        },
        query: gql`
            mutation PromoteBuild($buildId: Int!, $promotion: String!, $description: String, $dateTime: LocalDateTime) {
                createPromotionRunById(input: {
                    buildId: $buildId,
                    promotion: $promotion,
                    description: $description,
                    dateTime: $dateTime,
                }) {
                    errors {
                        message
                    }
                }
            }
        `,
        userNode: 'createPromotionRunById',
    })
}

export default function BuildPromoteDialog({buildPromoteDialog}) {
    return (
        <>
            <FormDialog dialog={buildPromoteDialog}>
                <Form.Item
                    name="promotionLevel"
                    label="Promotion level to promote to"
                    rules={[
                        {
                            required: true,
                            message: 'Promotion level is required.',
                        },
                    ]}
                >
                    <SelectPromotionLevel
                        branch={buildPromoteDialog?.context?.build?.branch}
                        useName={true}
                    />
                </Form.Item>
                <Form.Item
                    name="dateTime"
                    label="Date/time"
                    rules={[
                        {
                            required: true,
                            message: 'Promotion time is required.',
                        },
                    ]}
                >
                    <DatePicker showTime/>
                </Form.Item>
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