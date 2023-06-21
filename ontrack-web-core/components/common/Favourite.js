import {Typography} from "antd";
import {FaRegStar, FaStar} from "react-icons/fa";

const {Text} = Typography;

export default function Favourite({value}) {
    return (
        <>
            <Text style={{color: "orange"}}>
                {
                    value ?
                        <FaStar/> :
                        <FaRegStar/>
                }
            </Text>
        </>
    )
}