import {Typography} from "antd";
import SafeHTMLComponent from "@components/common/SafeHTMLComponent";

export default function AnnotatedDescription({
                                                 entity,
                                                 type,
                                                 disabled = true,
                                                 editable = false,
                                                 onChange,
                                             }) {
    const editableConfig = editable ? {
        text: entity.description ? entity.description : entity.annotatedDescription,
        onChange: onChange,
    } : undefined
    return entity.annotatedDescription ?
        <Typography.Text type={type} disabled={disabled} editable={editableConfig}>
            <SafeHTMLComponent
                htmlContent={entity.annotatedDescription}/>
        </Typography.Text> :
        (
            entity.description ?
                <Typography.Text type={type} disabled={disabled} editable={editableConfig}>{entity.description}</Typography.Text> :
                <Typography.Text type={type} disabled={disabled} editable={editableConfig}></Typography.Text>
        )

}