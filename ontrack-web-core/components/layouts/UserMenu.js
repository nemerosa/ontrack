import {Drawer} from "antd";
import {useState} from "react";

export function useUserMenu() {
    const [open, setOpen] = useState(false);

    return {
        open,
        setOpen,
    }
}

export default function UserMenu({userMenu}) {

    const onClose = () => {
        userMenu.setOpen(false)
    }

    return (
        <>
            <Drawer placement="right"
                    open={userMenu.open}
                    closable={false}
                    onClose={onClose}
            >
                <p>Sample</p>
            </Drawer>
        </>
    )
}
