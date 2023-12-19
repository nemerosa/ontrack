import {Select, Space} from "antd";
import MessageTypeIcon from "@components/common/MessageTypeIcon";

export default function SelectMessageType({value, onChange}) {

    const options = [
        {
            value: 'SUCCESS',
            label: <Space>
                <MessageTypeIcon type="SUCCESS"/>
                Success
            </Space>,
        },
        {
            value: 'INFO',
            label: <Space>
                <MessageTypeIcon type="INFO"/>
                Information
            </Space>,
        },
        {
            value: 'WARNING',
            label: <Space>
                <MessageTypeIcon type="WARNING"/>
                Warning
            </Space>,
        },
        {
            value: 'ERROR',
            label: <Space>
                <MessageTypeIcon type="ERROR"/>
                Error
            </Space>,
        },
    ]

    return (
        <>
            <Select
                value={value}
                onChange={onChange}
                style={{width: '16em'}}
                options={options}
            />
        </>
    )
}