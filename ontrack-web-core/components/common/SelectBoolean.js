import {Select} from "antd";

export default function SelectBoolean({id, value, onChange}) {
    return (
        <>
            <Select
                id={id}
                value={value}
                onChange={onChange}
                allowClear
                style={{width: '6em'}}
                options={[
                    {value: null, label: ''},
                    {value: true, label: 'Yes'},
                    {value: false, label: 'No'}
                ]}
            />
        </>
    )
}