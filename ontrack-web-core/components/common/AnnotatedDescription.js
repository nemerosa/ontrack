import {Typography} from "antd";
import SafeHTMLComponent from "@components/common/SafeHTMLComponent";

export default function AnnotatedDescription({entity, disabled = true}) {
    return entity.annotatedDescription ?
        <Typography.Text disabled={disabled}><SafeHTMLComponent
            htmlContent={entity.annotatedDescription}/></Typography.Text> :
        (
            entity.description ?
                <Typography.Text disabled={disabled}>{entity.description}</Typography.Text> :
                undefined
        )

}