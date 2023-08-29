import {Typography} from "antd";
import {FaRegStar, FaStar} from "react-icons/fa";

const {Text} = Typography;

export default function Favourite({value}) {
    return (
        <>
            <Text
                className="ot-action"
                style={{color: "orange"}}
                title={
                    value ? "Unselect as a favourite" : "Select as a favourite"
                }
            >
                {
                    value ?
                        <FaStar/> :
                        <FaRegStar/>
                }
            </Text>
        </>
    )
}