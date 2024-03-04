import {Tag, Typography} from "antd";

export default function MetricsValidationDataType({metrics}) {
    return (
        <>
            {
                Object.keys(metrics).map(name => {
                    const value = metrics[name]
                    return <Tag key={name}>
                        <Typography.Text strong>{name}</Typography.Text>: {value}
                    </Tag>
                })
            }
        </>
    )
}