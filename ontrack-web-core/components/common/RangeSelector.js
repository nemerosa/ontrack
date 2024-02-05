import {FaCheckSquare, FaSquare} from "react-icons/fa";
import {Typography} from "antd";
import {useState} from "react";

export default function RangeSelector({
                                          id,
                                          idPrefix = 'range',
                                          title,
                                          rangeSelection
                                      }) {

    const onSelect = () => {
        if (rangeSelection && rangeSelection.select) {
            rangeSelection.select(id)
        }
    }

    return (
        <Typography.Text>
            {
                (rangeSelection && rangeSelection.isSelected && rangeSelection.isSelected(id)) ?
                    <FaCheckSquare
                        id={`${idPrefix}-${id}`}
                        className="ot-action"
                        title={title}
                        onClick={onSelect}
                    /> :
                    <FaSquare
                        id={`${idPrefix}-${id}`}
                        className="ot-hover-transparent-10 ot-action"
                        title={title}
                        onClick={onSelect}
                    />
            }
        </Typography.Text>
    )
}