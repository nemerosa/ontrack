import {Button, Popconfirm} from "antd";
import {FaTrash} from "react-icons/fa";

export default function InlineConfirmCommand({title, confirm, icon = <FaTrash color="red"/>, onConfirm}) {
    return (
        <>
            <Popconfirm
                title={title}
                description={confirm}
                onConfirm={onConfirm}
            >
                <Button
                    type="text"
                    icon={icon}
                    title={title}
                />
            </Popconfirm>
        </>
    )
}