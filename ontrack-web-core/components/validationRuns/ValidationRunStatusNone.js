import ValidationRunStatusIcon from "@components/validationRuns/ValidationRunStatusIcon";
import {Space} from "antd";

export default function ValidationRunStatusNone({
                                                    disabled = false
                                                }) {
    return (
        <>
            <Space size={8} className={disabled ? undefined : "ot-command"}>
                <ValidationRunStatusIcon statusID={{id: 'NONE'}}/>
            </Space>
        </>
    )
}