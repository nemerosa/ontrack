import {Alert, Typography} from "antd";

export default function GlobalMessage({type, content}) {



    return (
        <>
            <Alert
                message={
                    <Typography.Text>{content}</Typography.Text>
                }
                showIcon
                type={type.toLowerCase()}
            />
        </>
    )
}