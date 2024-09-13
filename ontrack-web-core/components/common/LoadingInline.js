import {Space, Spin, Typography} from "antd";

export default function LoadingInline({loading, text = "Loading...", children}) {
    return (
        <>
            {
                loading && <Space>
                    <Spin spinning={true} size="small"/>
                    <Typography.Text>{text}</Typography.Text>
                </Space>
            }
            {
                !loading && children
            }
        </>
    )
}