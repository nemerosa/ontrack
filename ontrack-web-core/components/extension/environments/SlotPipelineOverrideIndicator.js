import {Popover} from "antd";
import {FaExclamationCircle} from "react-icons/fa";
import TimestampText from "@components/common/TimestampText";

export default function SlotPipelineOverrideIndicator({rule}) {
    return (
        <>
            {
                rule.overridden && rule.override &&
                <Popover
                    title="This rule was overridden"
                    content={
                        <>
                            By {rule.override.user} at <TimestampText value={rule.override.timestamp}/>
                        </>
                    }
                >
                    <FaExclamationCircle data-testid={`overridden-${rule.admissionRuleConfig.id}`} color="red"/>
                </Popover>
            }
        </>
    )
}