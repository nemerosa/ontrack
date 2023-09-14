import {useState} from "react";

export default function useRangeSelection() {

    // See https://react.dev/learn/updating-arrays-in-state
    const [selection, setSelection] = useState([])

    const isSelected = (x) => selection.indexOf(x) >= 0

    const isComplete = () => selection.length === 2 && selection[0] !== selection[1]

    const select = (x) => {
        // console.log(`Selecting ${x} in ${JSON.stringify(selection)}`)
        if (selection.length === 0) {
            setSelection([x])
        } else {
            const index = selection.indexOf(x)
            if (index >= 0) {
                setSelection(selection.filter(o => o !== x))
            } else if (selection.length === 1) {
                setSelection([...selection, x])
            } else {
                // Replacing the last element only (position 1)
                setSelection([selection[0], x])
            }
        }
        // console.log({newSelection: JSON.stringify(selection)})
    }

    return {
        /**
         * Access to the current selection (read-only)
         */
        selection,
        /**
         * Checks if an element is selected
         */
        isSelected,
        /**
         * Selects one element and adjust the section
         */
        select,
        /**
         * Checks is the selection is commplete
         */
        isComplete,
    }

}