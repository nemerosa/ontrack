import {useState} from "react";

export default function useSlotState() {

    const [slotState, setSlotState] = useState(0)

    const onSlotStateChanged = () => {
        setSlotState(state => state + 1)
    }

    return [
        slotState,
        onSlotStateChanged,
    ]
}