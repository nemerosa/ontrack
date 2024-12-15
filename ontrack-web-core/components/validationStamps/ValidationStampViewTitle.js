import ValidationStampImage from "@components/validationStamps/ValidationStampImage";
import {Space} from "antd";
import ValidationStampLink from "@components/validationStamps/ValidationStampLink";

export default function ValidationStampViewTitle({validationStamp, link = false}) {
    return (
        <>
            {
                validationStamp && <>
                    {
                        link && <ValidationStampLink validationStamp={validationStamp}/>
                    }
                    {
                        !link &&
                        <Space>
                            <ValidationStampImage validationStamp={validationStamp}/>
                            {validationStamp.name}
                        </Space>
                    }
                </>
            }
        </>
    )
}