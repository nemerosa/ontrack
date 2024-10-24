import {FaInfoCircle} from "react-icons/fa";
import {Popover} from "antd";

export default function PopoverInfoIcon({
                                            condition = true,
                                            icon = <FaInfoCircle/>,
                                            title = '',
                                            content,
                                        }) {
    return (
        <>
            {
                condition &&
                <Popover
                    title={title}
                    content={content}
                >
                    {icon}
                </Popover>
            }
        </>
    )
}
