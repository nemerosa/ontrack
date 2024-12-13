import {Space, Switch} from "antd";

export default function SlotBuildEligibilitySwitch({id = "show-eligible-builds", value, onChange}) {
    return (
        <>
            <Space>
                <Switch id={id} value={value} onChange={onChange}/>
                <label htmlFor={id}>Show all eligible builds</label>
            </Space>
        </>
    )
}