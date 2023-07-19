import {FaCheckSquare, FaSquare} from "react-icons/fa";
import {Typography} from "antd";
import {useState} from "react";

export const useRange = () => {
    const [range, setRange] = useState({first: undefined, second: undefined})
    return {
        range,
        onRangeChange: (id) => {
            if (!range.first) {
                setRange({
                    first: id,
                    second: range.second,
                })
            } else if (!range.second) {
                setRange({
                    first: range.first,
                    second: id,
                })
            } else if (range.first === id) {
                setRange({
                    first: undefined,
                    second: range.second,
                })
            } else if (range.second === id) {
                setRange({
                    first: range.first,
                    second: undefined,
                })
            }
        }
    }
}

export default function RangeSelector({
                                          id, title,
                                          range = {first: undefined, second: undefined},
                                          onRangeChange
                                      }) {

    const onSelect = () => {
        if (onRangeChange) onRangeChange(id)
    }

    return (
        <Typography.Text>
            {
                (range.first === id || range.second === id) ?
                    <FaCheckSquare
                        className="ot-action"
                        title={title}
                        onClick={onSelect}
                    /> :
                    <FaSquare
                        className="ot-hover-transparent-10 ot-action"
                        title={title}
                        onClick={onSelect}
                    />
            }
        </Typography.Text>
    )
}