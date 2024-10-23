import {useEffect, useState} from "react";
import {Select} from "antd";

function groupEligibleSlotsByEnvironment(eligibleSlots) {
    // Step 1: Initialize a list to group slots by environment name
    const entries = []

    // Step 2: Iterate over each slot
    eligibleSlots.forEach(eligibleSlot => {
        const envName = eligibleSlot.slot.environment.name
        const existingEntry = entries.find(entry => entry.environment.name === envName)
        if (!existingEntry) {
            entries.push({
                environment: eligibleSlot.slot.environment,
                slots: [eligibleSlot],
            })
        } else {
            // Add the slot to the existing list of slots
            existingEntry.slots.push(eligibleSlot)
        }
    })

    // Step 3: Convert the Map to an array and sort it by the environment's order
    entries.sort((a, b) => a.environment.order - b.environment.order);

    return entries
}

export default function SelectSlot({eligibleSlots, value, onChange}) {
    const [options, setOptions] = useState([])
    useEffect(() => {
        const entries = groupEligibleSlotsByEnvironment(eligibleSlots)
        const options = []
        entries.forEach(entry => {
            // Environment header
            const header = {
                label: entry.environment.name,
                title: entry.environment.name,
                options: [],
            };
            // Each eligible slot
            entry.slots.forEach(eligibleSlot => {
                const slot = eligibleSlot.slot
                let label = slot.environment.name
                if (slot.qualifier) {
                    label += ` [${slot.qualifier}]`
                }
                header.options.push({
                    label,
                    value: slot.id,
                    disabled: !eligibleSlot.eligible,
                })
            })
            options.push(header)
        })
        setOptions(options)
    }, [eligibleSlots])

    return (
        <>
            <Select
                onChange={onChange}
                value={value}
                options={options}
            />
        </>
    )
}