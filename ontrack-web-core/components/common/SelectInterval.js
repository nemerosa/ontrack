/**
 * Selection of a count of time and a unit of time (ie. 30 seconds).
 * @param value {count, unit}
 * @param onChange (value)
 */
import {InputNumber, Select, Space} from "antd";
import {useEffect, useState} from "react";
import {minutes, units} from "@components/common/IntervalUtils";

export default function SelectInterval({value, onChange}) {

    const [unit, setUnit] = useState(() => {
        if (value?.unit) {
            return units.find(it => it.value === value.unit) || minutes
        }
        return minutes
    })
    const [count, setCount] = useState(value?.count !== undefined ? value.count : 1)

    useEffect(() => {
        if (value?.unit) {
            const existingUnit = units.find(it => it.value === value.unit)
            if (existingUnit) {
                setUnit(existingUnit)
            } else {
                setUnit(minutes)
            }
        }
        if (value?.count !== undefined) {
            setCount(value.count)
        }
    }, [value?.unit, value?.count])

    const onUnitChange = (newUnit) => {
        if (unit.value !== newUnit) {
            const existingUnit = units.find(it => it.value === newUnit)
            if (existingUnit) {
                setUnit(existingUnit)
                const newCount = existingUnit.min
                setCount(newCount)
                if (onChange) onChange({
                    ...value,
                    unit: existingUnit.value,
                    count: newCount,
                })
            }
        }
    }

    const onCountChange = (newCount) => {
        setCount(newCount)
        if (onChange) onChange({
            ...value,
            count: newCount,
        })
    }

    return (
        <>
            <Space size={4}>
                <InputNumber
                    min={unit.min}
                    max={unit.max}
                    value={count}
                    onChange={onCountChange}
                    style={{
                        width: '5em'
                    }}
                />
                <Select
                    value={unit.value}
                    options={units}
                    onChange={onUnitChange}
                    style={{
                        width: '10em'
                    }}
                />
            </Space>
        </>
    )
}