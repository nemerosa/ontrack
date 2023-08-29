import {Typography} from "antd";
import {FaRegStar, FaStar} from "react-icons/fa";

const {Text} = Typography;

export default function Favourite({value, onClick}) {
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
                    value ?
                        <FaStar/> :
                        <FaRegStar/>
                }
            </Text>
        </>
    )
}