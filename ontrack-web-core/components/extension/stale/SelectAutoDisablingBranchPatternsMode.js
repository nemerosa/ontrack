import {Select} from "antd";
import {modes} from "@components/extension/stale/AutoDisablingBranchPatternsMode";

export default function SelectAutoDisablingBranchPatternsMode({value, onChange}) {
    return (
        <>
            <Select
                options={modes}
                value={value}
                onChange={onChange}
            />
        </>
    )
}