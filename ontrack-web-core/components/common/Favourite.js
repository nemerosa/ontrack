import {Typography} from "antd";
import {StarFilled, StarOutlined} from "@ant-design/icons";

const {Text} = Typography;

export default function Favourite({value}) {
    return (
        <>
            <Text style={{color: "orange"}}>
                {
                    value ?
                        <StarFilled/> :
                        <StarOutlined/>
                }
            </Text>
        </>
    )
}