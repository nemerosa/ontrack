import ValidationStamp from "@components/validationStamps/ValidationStamp";

export default function ValidationStampHeader({validationStamp}) {
    return (
        <>
            <ValidationStamp
                validationStamp={validationStamp}
                displayImage={true}
                displayText={false}
                displayTooltip={true}
            />
        </>
    )
}