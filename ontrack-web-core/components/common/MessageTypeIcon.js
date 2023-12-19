import {FaCheck, FaExclamationTriangle, FaInfoCircle, FaTimes} from "react-icons/fa";

export default function MessageTypeIcon({type}) {
    return (
        <>
            {
                type === 'ERROR' && <FaTimes color="red"/>
            }
            {
                type === 'WARNING' && <FaExclamationTriangle color="orange"/>
            }
            {
                type === 'INFO' && <FaInfoCircle color="blue"/>
            }
            {
                type === 'SUCCESS' && <FaCheck color="green"/>
            }
        </>
    )
}