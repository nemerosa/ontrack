import {Form, Input} from "antd";
import {prefixedFormName} from "@components/form/formUtils";
import SelectMultipleValidationStampNames from "@components/validationStamps/SelectMultipleValidationStampNames";
import SelectMultiplePromotionLevelNames from "@components/promotionLevels/SelectMultiplePromotionLevelNames";

export default function PropertyForm({prefix}) {

    return (
        <>
            <Form.Item
                label="Validation stamps"
                extra="List of validation stamps which trigger this promotion"
                name={prefixedFormName(prefix, 'validationStamps')}
            >
                <SelectMultipleValidationStampNames/>
            </Form.Item>
            <Form.Item
                label="Including"
                extra="Regular expression to include validation stamps by name"
                name={prefixedFormName(prefix, 'include')}
            >
                <Input/>
            </Form.Item>
            <Form.Item
                label="Excluding"
                extra="Regular expression to exclude validation stamps by name"
                name={prefixedFormName(prefix, 'exclude')}
            >
                <Input/>
            </Form.Item>
            <Form.Item
                label="Promotion levels"
                extra="List of promotion levels which trigger this promotion"
                name={prefixedFormName(prefix, 'promotionLevels')}
            >
                <SelectMultiplePromotionLevelNames/>
            </Form.Item>
        </>
    )
}