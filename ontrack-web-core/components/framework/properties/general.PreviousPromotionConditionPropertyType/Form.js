import {Form, Switch} from "antd";
import {prefixedFormName} from "@components/form/formUtils";

export default function PropertyForm({prefix}) {

    return (
        <>
            <Form.Item
                label="Depends"
                extra="If checked, the promotion must depend on a previous promotion"
                name={prefixedFormName(prefix, 'previousPromotionRequired')}
            >
                <Switch/>
            </Form.Item>
        </>
    )
}