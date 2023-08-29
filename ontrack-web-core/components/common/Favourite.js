import {Spin, Typography} from "antd";
import {FaRegStar, FaStar} from "react-icons/fa";
import {useState} from "react";

const {Text} = Typography;

/**
 *
 * @param value Value for the flag
 * @param onToggle Called when clicking the component. It expects to return a promise.
 */
export default function Favourite({value, onToggle}) {

    const [changing, setChanging] = useState(false)

    const onClick = async () => {
        if (onToggle) {
            setChanging(true)
            try {
                await onToggle()
            } finally {
                setChanging(false)
            }
        }
    }

    return (
        <>
            <Text
                className="ot-action"
                style={{color: "orange"}}
                title={
                    value ? "Unselect as a favourite" : "Select as a favourite"
                }
                onClick={onClick}
            >
                {
                    changing ?
                        <Spin size="small"/> :
                        (
                            value ?
                                <FaStar/> :
                                <FaRegStar/>
                        )
                }
            </Text>
        </>
    )
}