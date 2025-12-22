import {Space, Switch} from "antd";
import SelectPromotionLevelName from "@components/promotionLevels/SelectPromotionLevelName";
import SelectInterval from "@components/common/SelectInterval";

export default function PromotionWarningPeriodInput({value, onChange}) {

    const onPromotionLevelChange = (promotionLevel) => {
        if (onChange) {
            onChange({
                ...value,
                promotionLevel,
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
                {/* Selection of the promotion level */}
                <SelectPromotionLevelName value={value.promotionLevel} onChange={onPromotionLevelChange}/>
                {/* Optional period */}
                <Switch
                    checked={!!value.period}
                    onChange={onPeriodEnabledChange}
                    title="If a period is specified, a warning will be displayed if the promotion level has not been reached for this period."
                />
                {
                    value.period &&
                    <SelectInterval value={value.period} onChange={onPeriodChange}/>
                }
            </Space>
        </>
    )
}