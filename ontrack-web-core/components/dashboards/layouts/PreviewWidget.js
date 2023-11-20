import {Skeleton} from "antd";

export default function PreviewWidget() {
    return (
        <Skeleton style={{
            border: 'solid 1px #ccf',
            borderRadius: 4,
            padding: 4,
        }}/>
    )
}