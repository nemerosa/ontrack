import {Select, Space, Typography} from "antd";
import {useEffect, useState} from "react";

export default function SelectIssueService({value, onChange, self}) {

    const [options, setOptions] = useState([])
    const [selfAdded, setSelfAdded] = useState(false)

    useEffect(() => {
        if (self && !selfAdded) {
            setOptions(items => [...items, {
                value: "self",
                label: <Space>
                    <Typography.Text>{self}</Typography.Text>
                    <Typography.Text type="secondary">[self]</Typography.Text>
                </Space>,
            }])
            setSelfAdded(true)
        }
    }, [self, selfAdded])

    return (
        <>
            <Select
                options={options}
                value={value}
                onChange={onChange}
                allowClear={true}
            />
        </>
    )
}