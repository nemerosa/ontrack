/**
 * Selection of a count of time and a unit of time (ie. 30 seconds).
 * @param value {count, unit}
 * @param onChange (value)
 */
import {InputNumber, Select, Space} from "antd";
import {useEffect, useState} from "react";

const minutes = {
    value: 'M',
    label: 'Minutes',
    min: 0,
    max: 300,
}

export const toMilliSeconds = ({count, unit}) => {
    switch (unit) {
        case 'M': {
            return count * 60 * 1000
        }
        case 'H': {
            return count * 24 * 60 * 1000
        }
    }
    return 0
}

const units = [
    minutes,
    {
        value: 'H',
        label: 'Hours',
        min: 0,
        max: 24,
    },
]

export default function SelectInterval({value, onChange}) {

    const [unit, setUnit] = useState(minutes)
    const [count, setCount] = useState(1)

    useEffect(() => {
        if (value?.unit) {
            const existingUnit = units.find(it => it.value === value.unit)
            if (existingUnit) {
                setUnit(existingUnit)
            } else {
                setUnit(minutes)
            }
        }
    }, [value])

    useEffect(() => {
        if (value?.count) {
            setCount(value.count)
        }
    }, [value])

    const onUnitChange = (newUnit) => {
        if (unit.value !== newUnit) {
            const existingUnit = units.find(it => it.value === newUnit)
            if (existingUnit) {
                setUnit(existingUnit)
                setCount(existingUnit.min)
                if (onChange) onChange({
                    ...value,
                    unit: existingUnit.value,
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
                    value={unit}
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