import {Alert} from "antd";
import {useEffect, useState} from "react";

export default function CloseableAlert({id, message, type = "warning"}) {

    const key = `closeable-alert-${id}`
    const [closed, setClosed] = useState(true)
    useEffect(() => {
        const closed = localStorage.getItem(key) === 'yes'
        setClosed(localStorage.getItem(key) === 'yes')
    }, [key])

    const onClose = () => {
        localStorage.setItem(key, 'yes')
        setClosed(true)
    }

    return (
        <>
            {
                !closed &&
                <Alert
                    showIcon={true}
                    closable={true}
                    message={message}
                    type={type}
                    onClose={onClose}
                />
            }
        </>
    )
}