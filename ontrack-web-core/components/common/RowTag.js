import {Tag} from "antd";

export default function RowTag({children}) {
    return (
        <Tag
            style={{
                padding: 6,
            }}
        >
            {children}
        </Tag>
    )
}