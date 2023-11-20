import {Space} from "antd";
import PreviewWidget from "@components/dashboards/layouts/PreviewWidget";

export default function DefaultLayoutPreview() {
    return (
        <Space direction="vertical" style={{
            width: '100%',
        }}>
                <div style={{
                    width: '100%',
                }}>
                    <PreviewWidget/>
                </div>
        </Space>
    )
}