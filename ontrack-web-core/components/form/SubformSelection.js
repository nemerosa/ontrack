import {Form} from "antd";
import Well from "@components/common/Well";

export default function SubformSelection({
                                             form,
                                             idLabel,
                                             idName,
                                             idItem,
                                             formLabel,
                                             formItem,
                                         }) {

    const idValue = Form.useWatch(idName, form)

    return (
        <>
            <Form.Item
                label={idLabel}
                name={idName}
            >
                {idItem}
            </Form.Item>
            {
                idValue &&
                <Form.Item
                    label={formLabel}
                >
                    <Well>
                        {
                            formItem(idValue)
                        }
                    </Well>
                </Form.Item>
            }
        </>
    )
}