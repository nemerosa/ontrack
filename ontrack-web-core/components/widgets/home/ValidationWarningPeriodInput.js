import {Space, Switch} from "antd";
import SelectInterval from "@components/common/SelectInterval";
import SelectValidationStampName from "@components/validationStamps/SelectValidationStampName";

export default function ValidationWarningPeriodInput({value, onChange}) {

    const onValidationStampChange = (validationStamp) => {
        if (onChange) {
            onChange({
                ...value,
                validationStamp,
            })
        }
    }

    const onPeriodChange = (period) => {
        if (onChange) {
            onChange({
                ...value,
                period,
            })
        }
    }

    const onPeriodEnabledChange = (enabled) => {
        if (onChange) {
            if (enabled) {
                onChange({
                    ...value,
                    period: {
                        count: 1,
                        unit: 'D',
                    }
                })
            } else {
                const newValue = {...value}
                delete newValue.period
                onChange(newValue)
            }
        }
    }

    return (
        <>
            <Space>
                {/* Selection of the validation stamp */}
                <SelectValidationStampName value={value.validationStamp} onChange={onValidationStampChange}/>
                {/* Optional period */}
                <Switch
                    checked={!!value.period}
                    onChange={onPeriodEnabledChange}
                    title="If a period is specified, a warning will be displayed if the validation stamp has not been passed for this period."
                />
                {
                    value.period &&
                    <SelectInterval value={value.period} onChange={onPeriodChange}/>
                }
            </Space>
        </>
    )
}