import {Space} from "antd";
import {FaArrowRight} from "react-icons/fa";

export default function TransitionBox({before, after}) {
    return (
        <>
            <Space>
                {before}
                <FaArrowRight/>
                {after}
            </Space>
        </>
    )
}