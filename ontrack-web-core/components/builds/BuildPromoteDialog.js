import FormDialog, {useFormDialog} from "@components/form/FormDialog";
import {DatePicker, Form, Input} from "antd";
import SelectPromotionLevel from "@components/promotionLevels/SelectPromotionLevel";
import dayjs from "dayjs";

const { TextArea } = Input;

export function useBuildPromoteDialog(config) {
    return useFormDialog({
        ...config,
        init: (form, context) => {
            form.setFieldsValue({
                promotionLevel: context.promotionLevel?.id,
                dateTime: dayjs(),
            })
        },
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