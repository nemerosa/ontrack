import {Typography} from "antd";
import SafeHTMLComponent from "@components/common/SafeHTMLComponent";

export default function AnnotatedDescription({
                                                 entity,
                                                 disabled = true,
                                                 editable = false,
                                                 onChange,
                                             }) {
    const editableConfig = editable ? {
        text: entity.description ? entity.description : entity.annotatedDescription,
        onChange: onChange,
    } : undefined
    return entity.annotatedDescription ?
        <Typography.Text disabled={disabled} editable={editableConfig}>
            <SafeHTMLComponent
                htmlContent={entity.annotatedDescription}/>
        </Typography.Text> :
        (
            entity.description ?
                <Typography.Text disabled={disabled} editable={editableConfig}>{entity.description}</Typography.Text> :
                <Typography.Text disabled={disabled} editable={editableConfig}></Typography.Text>
        )

}